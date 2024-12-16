package aucta.dev.mercator_core.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDTO {
    private Long id;

    private byte[] imageData;
}
