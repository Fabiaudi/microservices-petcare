package com.ms.cadastro.external;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ms.cadastro.enums.Species;


@Component // Diz pro Spring: "Ei, cria automaticamente uma cópia dessa classe quando o sistema iniciar!"
public class PetApi {

    private final String apiKey;
    private final String dogBreedsUrl;
    private final String catBreedsUrl;
    // URL base onde vivem as imagens dos doguinhos (e serve para buscar imagens específicas por ID)
    private final String imageUrl = "https://api.thedogapi.com/v1/images/";
    // Esse é o telefone que faz ligações para os sistemas externos (as APIs)
    private final RestTemplate restTemplate;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    public PetApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // Pega a chave da API de forma segura (das variáveis de ambiente do sistema)
        this.apiKey = System.getenv("x-api-key");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new IllegalStateException("API Key is not set in environment variables!");
        }

        // URLs com a chave já embutida (assim ele não precisa mandar por header)
        this.dogBreedsUrl = "https://api.thedogapi.com/v1/breeds?api_key=" + this.apiKey;
        this.catBreedsUrl = "https://api.thecatapi.com/v1/breeds?api_key=" + this.apiKey;
    }

    // Método que busca todas as raças disponíveis da espécie solicitada (DOG ou CAT)
    public List<PetApiDto> getBreeds(Species species) {
        // Decide qual URL usar com base na espécie (cachorro ou gato)
        String baseUrl = (species == Species.DOG) ? dogBreedsUrl : catBreedsUrl;

        // Usa o telefone (RestTemplate) para fazer uma ligação GET para a API e pega a lista de raças
        // E se a API estiver ocupada? A gente tenta de novo com executeWithRetry!
        PetApiDto[] response = executeWithRetry(() -> {
            ResponseEntity<PetApiDto[]> responseEntity = restTemplate.exchange(
                baseUrl, // para onde vai ligar
                HttpMethod.GET, // qual tipo de ligação? GET = só quer saber, não mudar nada
                null, // nenhum pacote de envio (sem headers nem corpo)
                PetApiDto[].class // esperamos um pacote com uma lista de raças
            );
            return responseEntity.getBody(); // abre o pacote e vê o que tem dentro
        });

        // Se não veio nada, algo deu errado: a API respondeu vazio ou nulo
        if (response == null || response.length == 0) {
            throw new IllegalStateException("No breeds found.");
        }

        // Se veio, converte o array para uma lista Java, que é mais fácil de trabalhar
        return Arrays.asList(response);
    }

    // Esse método busca a imagem da raça pedida, dependendo da espécie (cachorro ou gato)
    public String getImageFromBreed(Species species, String breed) {
        // Primeiro, pega todas as raças dessa espécie
        List<PetApiDto> breeds = getBreeds(species);

        // Se for cachorro...
        if (species == Species.DOG) {
            for (PetApiDto b : breeds) {
                // Se o nome bater com o pedido, e tiver ID de imagem...
                if (b.getName().equalsIgnoreCase(breed) && b.getReference_image_id() != null) {
                    // Pega a imagem pelo ID
                    return getImageById(b.getReference_image_id());
                }
            }
        } else {
            // Se for gato...
            for (PetApiDto b : breeds) {
                if (b.getName().equalsIgnoreCase(breed)) {
                    // Se já tiver uma imagem com URL direto, usamos ela
                    if (b.getImage() != null && b.getImage().getUrl() != null) {
                        return b.getImage().getUrl();
                    }
                }
            }
        }

        // Se chegou aqui, é porque não achou imagem da raça pedida
        return " No image found for: " + breed;
    }

    // Esse método busca a imagem diretamente pelo ID (usado para cachorros)
    public String getImageById(String imageId) {
        if (imageId == null || imageId.isEmpty()) {
            return null; // Se não tem ID, não tem imagem
        }

        // Monta a URL com o ID da imagem
        String url = imageUrl + imageId;

        // Faz uma ligação GET pra pegar a imagem
        PetApiDto response = executeWithRetry(() -> restTemplate.getForObject(url, PetApiDto.class));

        // Se recebeu resposta, devolve a URL da imagem. Se não, devolve null.
        return response != null ? response.getUrl() : null;
    }

    // Essa parte é a magia do "retry" — tenta a ligação até 3 vezes se der erro 429 (limite de chamadas atingido)
    private <T> T executeWithRetry(Supplier<T> request) {
        int attempts = 0;

        while (attempts < MAX_RETRIES) {
            try {
                // Tenta fazer a ligação
                return request.get();
            } catch (HttpClientErrorException e) {
                // Se o erro for "Too Many Requests" (status 429)...
                if (e.getStatusCode().value() == 429) {
                    attempts++;
                    if (attempts < MAX_RETRIES) {
                        try {
                            // Espera um pouco e tenta de novo
                            System.out.println("Too many requests. Retrying in " + RETRY_DELAY_MS + "ms...");
                            TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
                        } catch (InterruptedException ie) {
                            // Se alguém interromper a espera, marcamos como interrompido
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        // Se tentou 3 vezes e nada, desiste e lança um erro
                        throw new IllegalStateException("Limit of attempts exceeded.");
                    }
                } else {
                    // Se for qualquer outro erro, não tentamos de novo
                    throw e;
                }
            }
        }

        return null; // nunca deve chegar aqui
    }
}