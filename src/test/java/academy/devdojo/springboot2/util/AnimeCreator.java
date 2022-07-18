package academy.devdojo.springboot2.util;

import academy.devdojo.springboot2.domain.Anime;

public class AnimeCreator {


    public static Anime createAnimeToBeSaved(){
        return Anime.builder()
                .name("Hajime no Ippo Aero")
                .build();
    }

    public static Anime createAnimeValid(){
        return Anime.builder()
                .name("Hajime no Ippo")
                .id(1l)
                .build();
    }


    public static Anime createAnimeValidUpdatedAnime(){
        return Anime.builder()
                .id(1l)
                .name("Hajime no Dragon")
                .build();
    }
}
