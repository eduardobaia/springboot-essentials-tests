package academy.devdojo.springboot2.util;

import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;

public class AnimePutRequestBodyCreator {

    public static AnimePutRequestBody createAnimePutRequestBody(){
        return AnimePutRequestBody.builder()
                .name(AnimeCreator.createAnimeValidUpdatedAnime().getName())
                .id(AnimeCreator.createAnimeValidUpdatedAnime().getId())
                .build();
    }
}
