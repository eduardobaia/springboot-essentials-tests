package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.text.html.Option;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {
    //The classe I want to test
    @InjectMocks
    private AnimeService animeService;
    @Mock
    private AnimeRepository animeRepositoryMock;
    //The mock for the class that are insed controller. Classe  that use IjectMocks.

    @BeforeEach
    void setUp(){
        PageImpl<Anime> animePage = new PageImpl<Anime>(List.of(AnimeCreator.createAnimeValid()));
        BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(animePage);

        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(List.of(AnimeCreator.createAnimeValid()));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createAnimeValid()));


        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createAnimeValid()));


        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn( AnimeCreator.createAnimeValid());


        BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
    }


    @Test
    @DisplayName("List return list of anime inside page when sucess.")
    void list_returnListOfAnimesInsidePageObject_whenSucessful(){

        String expectedName = AnimeCreator.createAnimeValid().getName();

        Page<Anime> animePage= animeService.listAll(PageRequest.of(1,1));

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

        List<Anime> animes= animeService.listAllNonPageable();


        Assertions.assertThat(animes).isNotNull()
                .isNotEmpty()
                .hasSize(1);


        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }


    @Test
    @DisplayName("Find by Id.")
    void list_returnAnimesFIndById_whenSucessful(){

        Long expectedId = AnimeCreator.createAnimeValid().getId();

        Anime anime= animeService.findByIdOrThrowBadRequestException(1);


        Assertions.assertThat(anime).isNotNull();


        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }


    @Test
    @DisplayName("Find by Name.")
    void list_returnAnimesFindByNamewhenSucessful(){

        String expectedName = AnimeCreator.createAnimeValid().getName();

        List<Anime> animes= animeService.findByName("anime");


        Assertions.assertThat(animes).isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }


    @Test
    @DisplayName("Find by Name return empty list when anime not found.")
    void findByName_returns_emptyList_whenAnimeNotFound(){

        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());


        List<Anime> animes= animeService.findByName("anime");


        Assertions.assertThat(animes).isNotNull()
                .isEmpty() ;
    }


    @Test
    @DisplayName("Save Anime Return anime when Sucessful.")
    void saveAnime(){

        Anime anime= animeService.save(AnimePostRequestBodyCreator.createAnimePostRequestBody());


        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime).isEqualTo(AnimeCreator.createAnimeValid());
    }


    @Test
    @DisplayName("replace Anime Return anime when Sucessful.")
    void updateAnime(){

        animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());


        Assertions.assertThatCode( () ->  animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
                .doesNotThrowAnyException();


    }

    @Test
    @DisplayName("delete Anime")
    void deleteAnime(){

        animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());


        Assertions.assertThatCode( () ->  animeService.delete(1))
                .doesNotThrowAnyException();



    }



}