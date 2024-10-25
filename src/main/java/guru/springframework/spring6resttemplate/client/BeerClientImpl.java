package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;

    public static final String GET_BEER_PATH = "/api/v1/beer";
    public static final String GET_BEER_BY_ID_PATH = "/api/v1/beer/{beerId}";


    @Override
    public void deleteBeer(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        restTemplate.delete(GET_BEER_BY_ID_PATH, beerId);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO beerDTO) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.put(GET_BEER_BY_ID_PATH, beerDTO, beerDTO.getId());
        return getBeerById(beerDTO.getId());
    }



    @Override
    public BeerDTO createBeer(BeerDTO newDto) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        URI uri = restTemplate.postForLocation(GET_BEER_PATH, newDto);
        return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        return restTemplate.getForObject(GET_BEER_BY_ID_PATH, BeerDTO.class, beerId);
    }


    @Override
    public Page<BeerDTO> listBeers() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = getUriComponentsBuilder(null, null, null, null, null);

        return getBeerDTOPage(restTemplate, uriComponentsBuilder);
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName) {

        return listBeers(beerName, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> listBeers(BeerStyle beerStyle) {
        return listBeers(null, beerStyle, null, null, null);
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        var restTemplate = restTemplateBuilder.build();

        var uriComponentsBuilder = getUriComponentsBuilder(beerName, beerStyle, showInventory, pageNumber, pageSize);

        return getBeerDTOPage(restTemplate, uriComponentsBuilder);
    }

    private BeerDTOPageImpl getBeerDTOPage(RestTemplate restTemplate, UriComponentsBuilder uriComponentsBuilder) {
        var response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

        return response.getBody();
    }

    private UriComponentsBuilder getUriComponentsBuilder(String beerName, BeerStyle beerStyle, Boolean showInventory,
                                                         Integer pageNumber, Integer pageSize) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        if (beerName != null) {
            uriComponentsBuilder.queryParam("beerName", beerName);
        }

        if (beerStyle != null) {
            uriComponentsBuilder.queryParam("beerStyle", beerStyle.name());
        }

        if (showInventory != null) {
            uriComponentsBuilder.queryParam("showInventory", showInventory);
        }

        if (pageNumber != null) {
            uriComponentsBuilder.queryParam("pageNumber", pageNumber);
        }

        if (pageSize != null) {
            uriComponentsBuilder.queryParam("pageSize", pageSize);
        }

        return uriComponentsBuilder;
    }
}
