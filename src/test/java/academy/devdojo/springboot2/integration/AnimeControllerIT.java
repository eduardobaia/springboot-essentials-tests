package academy.devdojo.springboot2.integration;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.User;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.repository.UserRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)  //to clean database before each test.
public class AnimeControllerIT {



//    @LocalServerPort
//    private int port;

    @Autowired
    private AnimeRepository animeRepository;


    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplate;


    @Autowired
    private UserRepository userRepository;

    private static final  User USER = User.builder()
            .name("edu")
            .password("{bcrypt}$2a$10$1.txFcHf4Z7FMBzzsZM6C./rLzHIk7WrBewwDLl3Km1ieNwAbOL2q")
            .username("eduardo")
            .authorities("ROLE_USER")
            .build();

    private static final  User ADMIN = User.builder()
            .name("edu")
            .password("{bcrypt}$2a$10$1.txFcHf4Z7FMBzzsZM6C./rLzHIk7WrBewwDLl3Km1ieNwAbOL2q")
            .username("edu")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    @TestConfiguration
    @Lazy
    static class Confi{

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port){


            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("eduardo", "test");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port){


            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("edu", "test");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }


    @Test
    @DisplayName("List return list of anime inside page when sucess.")
    void list_returnListOfAnimesInsidePageObject_whenSucessful(){

        Anime animeSaved= animeRepository.save(AnimeCreator.createAnimeValid());
        String expectedName = animeSaved.getName();


        userRepository.save(USER);

       PageableResponse<Anime> animePage=  testRestTemplate.exchange("/animes", HttpMethod.GET, null, new ParameterizedTypeReference<PageableResponse<Anime>>() {
        }).getBody();
      //  Page<Anime> animePage= animeController.list(null).getBody();

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage.toList()).isNotNull();
        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }



    @Test
    @DisplayName("List return  list of all  anime inside page when sucess.")
    void list_returnListAllOfAnimesInsidePageObject_whenSucessful(){

        Anime animeSaved= animeRepository.save(AnimeCreator.createAnimeValid());
        String expectedName = animeSaved.getName();


        List<Anime> animes=  testRestTemplate.exchange("/animes/all", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        }).getBody();
        //  Page<Anime> animePage= animeController.list(null).getBody();

        Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);


        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }


   @Test
   @DisplayName("Find by Id.")
   void list_returnAnimesFIndById_whenSucessful(){
       Anime animeSaved= animeRepository.save(AnimeCreator.createAnimeValid());

       Long expectedId =animeSaved.getId();

       Anime anime= testRestTemplate.getForObject("/animes/{id}", Anime.class, expectedId);


       Assertions.assertThat(anime).isNotNull();


       Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
   }


    @Test
    @DisplayName("Find by Name.")
    void list_returnAnimesFindByNamewhenSucessful(){
        Anime animeSaved= animeRepository.save(AnimeCreator.createAnimeValid());
        String expectedName =animeSaved.getName();

        String url = String.format("/animes/find?name=%s", expectedName);
        List<Anime> animes=  testRestTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        }).getBody();

        Assertions.assertThat(animes).isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }


    @Test
    @DisplayName("Find by Name return empty list when anime not found.")
    void findByName_returns_emptyList_whenAnimeNotFound(){

     Anime animeSaved= animeRepository.save(AnimeCreator.createAnimeValid());

        List<Anime> animes=  testRestTemplate.exchange("/animes/find?name=dbzname", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        }).getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();


    }


  @Test
  @DisplayName("Save Anime Return anime when Sucessful.")
  void saveAnime(){

      AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
      Anime anim= animeRepository.save(AnimeCreator.createAnimeValid());


      ResponseEntity<Anime> animeResponseEntity= testRestTemplate.postForEntity("/animes", animePostRequestBody, Anime.class);


      Assertions.assertThat(animeResponseEntity).isNotNull();

      Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
      Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
  }


   @Test
   @DisplayName("replace Anime Return anime when Sucessful.")
   void updateAnime(){
       Anime animeSaved= animeRepository.save(AnimeCreator.createAnimeValid());

       animeSaved.setName("Johnn");

       ResponseEntity<Void> animeResponseEntity= testRestTemplate.exchange("/animes", HttpMethod.PUT,new HttpEntity<>(animeSaved) ,Void.class);


       Assertions.assertThat(animeResponseEntity).isNotNull();

       Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

   }

    @Test
    @DisplayName("delete Anime")
    void deleteAnime(){

        Anime animeSaved= animeRepository.save(AnimeCreator.createAnimeValid());


        ResponseEntity<Void> animeResponseEntity= testRestTemplate.exchange("/animes/{id}", HttpMethod.DELETE,null ,Void.class, animeSaved.getId());


        Assertions.assertThat(animeResponseEntity).isNotNull();

        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }




}
