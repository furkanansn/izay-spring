package ariservice.izay.category.dto;

import lombok.Data;

@Data
public class CategoryUpdateDto {
	
	private Long id;
	private String imageBase64;
	private String nameTr;
	private String nameEn;
	private String subTitleTr;
	private String descriptionTr;
	private String subTitleEn;
	private String descriptionEn;

}
