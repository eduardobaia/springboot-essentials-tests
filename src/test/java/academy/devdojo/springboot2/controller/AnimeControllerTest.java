package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;


@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    //The classe I want to test
    @InjectMocks
    private AnimeController animeController;
    //The mock for the class that are insed controller. Classe  that use IjectMocks.
    @Mock
    private AnimeService animeServiceMock;


    @BeforeEach
    void setUp(){
        PageImpl<Anime> animePage = new PageImpl<Anime>(List.of(AnimeCreator.createAnimeValid()));
        BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any()))
                .thenReturn(animePage);

        BDDMockito.when(animeServiceMock.listAllNonPageable())
                .thenReturn(List.of(AnimeCreator.createAnimeValid()));

        BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
                .thenReturn(AnimeCreator.createAnimeValid());


        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createAnimeValid()));


        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                .thenReturn( AnimeCreator.createAnimeValid());

        BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));
        BDDMockito.doNothing().when(animeServiceMock).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("List return list of anime inside page when sucess.")
    void list_returnListOfAnimesInsidePageObject_whenSucessful(){

        String expectedName = AnimeCreator.createAnimeValid().getName();

        Page<Anime> animePage= animeController.list(null).getBody();

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

        String expectedName = AnimeCreator.createAnimeValid().getName();

        List<Anime> animes= animeController.listAll().getBody();


        Assertions.assertThat(animes).isNotNull()
                .isNotEmpty()
                .hasSize(1);


        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }


    @Test
    @DisplayName("Find by Id.")
    void list_returnAnimesFIndById_whenSucessful(){

        Long expectedId = AnimeCreator.createAnimeValid().getId();

        Anime anime= animeController.findById(1).getBody();


        Assertions.assertThat(anime).isNotNull();


        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }


    @Test
    @DisplayName("Find by Name.")
    void list_returnAnimesFindByNamewhenSucessful(){

        String expectedName = AnimeCreator.createAnimeValid().getName();

        List<Anime> animes= animeController.findByName("anime").getBody();


        Assertions.assertThat(animes).isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }


    @Test
    @DisplayName("Find by Name return empty list when anime not found.")
    void findByName_returns_emptyList_whenAnimeNotFound(){

        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());


        List<Anime> animes= animeController.findByName("anime").getBody();


        Assertions.assertThat(animes).isNotNull()
                .isEmpty() ;
    }


    @Test
    @DisplayName("Save Anime Return anime when Sucessful.")
    void saveAnime(){

         Anime anime= animeController.save(AnimePostRequestBodyCreator.createAnimePostRequestBody()).getBody();


        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime).isEqualTo(AnimeCreator.createAnimeValid());
    }


    @Test
    @DisplayName("replace Anime Return anime when Sucessful.")
    void updateAnime(){

     animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()).getBody();


        Assertions.assertThatCode( () ->  animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
                        .doesNotThrowAnyException();

        ResponseEntity<Void> entity = animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());
        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


    }

    @Test
    @DisplayName("delete Anime")
    void deleteAnime(){

        animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()).getBody();


        Assertions.assertThatCode( () ->  animeController.delete(1))
                .doesNotThrowAnyException();

        ResponseEntity<Void> entity = animeController.delete(1);
        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


    }




}