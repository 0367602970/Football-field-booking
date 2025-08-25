package vti.group10.football_booking.service.owner;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.FieldImageRepository;
import vti.group10.football_booking.repository.FootballFieldRepository;

@Service
@RequiredArgsConstructor
public class FieldImageService  {
    private final FieldImageRepository imageRepo;
    private final FootballFieldRepository fieldRepo;

    public List<String> addImages(Long fieldId, List<String> imageUrls) {
        FootballField field = fieldRepo.findById(fieldId)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        List<FieldImage> images = new ArrayList<>();
        for (String url : imageUrls) {
            FieldImage img = FieldImage.builder()
                    .imageUrl(url)
                    .field(field)
                    .build();
            images.add(img);
        }
        imageRepo.saveAll(images);

        return images.stream().map(FieldImage::getImageUrl).toList();
    }

    public void deleteImage(Long imageId) {
        imageRepo.deleteById(imageId);
    }

    public List<String> getImages(Long fieldId) {
        return imageRepo.findByFieldId(fieldId)
                .stream()
                .map(FieldImage::getImageUrl)
                .toList();
    }
}
