package az.company.turbo.service.impl;

import az.company.turbo.dto.ProductDto;
import az.company.turbo.entity.*;
import az.company.turbo.repository.*;
import az.company.turbo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final CityRepository cityRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final ContactInfoRepository contactInfoRepository;

    public ProductServiceImpl
            (
                    ProductRepository productRepository
                    , BrandRepository brandRepository
                    , ModelRepository modelRepository
                    , CityRepository cityRepository
                    , FuelTypeRepository fuelTypeRepository
                    , ContactInfoRepository contactInfoRepository
            ) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.cityRepository = cityRepository;
        this.fuelTypeRepository = fuelTypeRepository;
        this.contactInfoRepository = contactInfoRepository;
    }

    @Override
    public ResponseEntity<?> create(ProductDto productDto) {
        ProductEntity entity = new ProductEntity();
        entity.setModel(checkModel(productDto));
        entity.setContactInfo(checkContact(productDto));
        entity.setFuelType(checkFuelType(productDto));
        entity.setCreditStatus(productDto.isCreditStatus());
        entity.setDrive(productDto.getDrive());
        entity.setEnginePower(productDto.getEnginePower());
        entity.setPhoto(productDto.getPhoto());
        entity.setMileage(productDto.getMileage());
        entity.setPrice(productDto.getPrice());
        entity.setValyuta(productDto.getValyuta());
        entity.setBarterStatus(productDto.isCreditStatus());
        entity.setDescription(productDto.getDesc());
        entity.setReleaseDate(productDto.getReleaseDate());
        entity= productRepository.save(entity);
        productDto=convertFromEntityToDto(entity);
        return ResponseEntity.ok(productDto);
    }


    @Override
    public ResponseEntity<?> delete(Integer id) {
        ProductEntity entity = productRepository.findById(id).orElseThrow(() -> new RuntimeException("product id not founded."));
        productRepository.delete(entity);
        return ResponseEntity.ok(String.format("Raw with %s id successfully deleted.", id));
    }

    @Override
    public ResponseEntity<?> update(ProductDto productDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> get() {

        return null;
    }

    private ProductDto convertFromEntityToDto(ProductEntity entity) {
        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        ModelServiceImpl modelService = new ModelServiceImpl(modelRepository, brandRepository);
        dto.setModeldto(modelService.convertFromEntityToDto(entity.getModel()));
        ContactInfoServiceImpl infoService=new ContactInfoServiceImpl(contactInfoRepository,cityRepository);
        dto.setContactInfoDto(infoService.convertFromEntityToDto(entity.getContactInfo()));
        FuelTypeServiceImle fuelService=new FuelTypeServiceImle(fuelTypeRepository);
        dto.setFuelType(fuelService.convertFromEntityToDto(entity.getFuelType()));
        dto.setEnginePower(entity.getEnginePower());
        dto.setDrive(entity.getDrive());
        dto.setDesc(entity.getDescription());
        dto.setBarter(entity.isBarterStatus());
        dto.setCreditStatus(entity.isCreditStatus());
        dto.setMileage(entity.getMileage());
        dto.setReleaseDate(entity.getReleaseDate());
        dto.setValyuta(entity.getValyuta());
        dto.setPhoto(entity.getPhoto());
        dto.setPrice(entity.getPrice());
        return dto;
    }

    private ProductEntity getById(Integer id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
    }
    private FuelTypeEntity checkFuelType(ProductDto productDto){
        return fuelTypeRepository
                .findById(productDto.getFuelType().getId())
                .orElseGet(() -> {
                  FuelTypeEntity fuelType =new FuelTypeEntity();
                  fuelType.setName(productDto.getFuelType().getName());
                  fuelType=fuelTypeRepository.save(fuelType);
                  return  fuelType;
        });

    }

    private ModelEntity checkModel(ProductDto productDto) {
        return modelRepository
                .findById(productDto.getModeldto().getId())
                .orElseGet(() -> {
                    ModelEntity modelEntity = new ModelEntity();
                    modelEntity.setName(productDto.getModeldto().getName());
                    BrandEntity brand = brandRepository
                            .findById(productDto.getModeldto().getBrandDto().getId())
                            .orElseGet(() -> {
                                BrandEntity brandEntity = new BrandEntity();
                                brandEntity.setName(productDto.getModeldto().getBrandDto().getName());
                                brandEntity = brandRepository.save(brandEntity);
                                return brandEntity;
                            });
                    modelEntity.setBrandEntity(brand);
                    modelEntity = modelRepository.save(modelEntity);
                    return modelEntity;

                });
    }

    private ContactInfoEntity checkContact(ProductDto productDto) {
        return contactInfoRepository
                .findById(productDto.getContactInfoDto().getId())
                .orElseGet(() -> {
                    ContactInfoEntity entity = new ContactInfoEntity();
                    entity.setName(productDto.getContactInfoDto().getName());
                    entity.setEmail(productDto.getContactInfoDto().getEmail());
                    entity.setPhone(productDto.getContactInfoDto().getPhone());
                    entity.setCityEntity(cityRepository
                            .findById(productDto.getContactInfoDto().getId())
                            .orElseGet(() -> {
                                CityEntity cityEntity = new CityEntity();
                                cityEntity.setName(productDto.getContactInfoDto().getCity().getName());
                                cityEntity = cityRepository.save(cityEntity);
                                return cityEntity;
                            }));
                    entity = contactInfoRepository.save(entity);
                    return entity;
                });
    }


}
