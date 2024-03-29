package ariservice.izay.documents;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import ariservice.izay.documents.dto.AddDocumentDto;
import ariservice.izay.documents.dto.UpdateDocumentDto;
import ariservice.izay.documents.entity.Document;
import ariservice.izay.documents.repository.DocumentRepository;
import ariservice.izay.io.IoUtil;
import ariservice.izay.security.JwtHelper;
import ariservice.izay.util.ApiPaths;
import ariservice.izay.util.GeneralResponse;
import ariservice.izay.util.SlugUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping(ApiPaths.DocumentCtrl.CTRL)

public class DocumentController {
	
	private final DocumentRepository repo;
	private final ModelMapper modelMapper;
	JwtHelper jwtHelper;

	public DocumentController(DocumentRepository repo, ModelMapper modelMapper) {
		super();
		this.repo = repo;
		this.modelMapper = modelMapper;
		jwtHelper  = new JwtHelper();
	}





	@GetMapping()
	ResponseEntity<GeneralResponse> getAllDocs(){
		try {
			return ResponseEntity.ok(new GeneralResponse(true,repo.findAll(),""));

		} catch (Exception e) {
			
			String errorString = "DocsController on getAllDocs "  +  e.getMessage();
			return ResponseEntity.ok(new GeneralResponse(false,null,errorString));

		}
		
	}
	
	@PostMapping("/add")
	ResponseEntity<GeneralResponse> addDocs(@RequestHeader(value = "Authorization", required = false)String token,@RequestBody AddDocumentDto addDocumentDto){
		
		try {
			
			String aString = jwtHelper.verifyJwt(token);
			
			Document document = modelMapper.map(addDocumentDto, Document.class);
			
			String imagePath = IoUtil.decoder(addDocumentDto.getImageBase64());
			
			String pdfPath = IoUtil.decoder(addDocumentDto.getPdfBase64());
			
			document.setSlug(SlugUtil.toSlug(document.getNameTr()));
			
			document.setImagePath(imagePath);
			document.setPdfPath(pdfPath);
			

			
			
			return ResponseEntity.ok(new GeneralResponse(true,repo.save(document),""));
			
		} catch (Exception e) {
			
			String errorString = "DocsController on addDocs "  +  e.getMessage();
			return ResponseEntity.ok(new GeneralResponse(false,null,errorString));
		}
		

	}
	
	
	@PostMapping("/update")
	ResponseEntity<GeneralResponse> updateDocs(@RequestHeader(value = "Authorization", required = false)String token,@RequestBody UpdateDocumentDto dto){
		
		try {
			String aString = jwtHelper.verifyJwt(token);

			Optional<Document> document = repo.findById(dto.getId());
			if(document.isPresent()) {
				
				Document document2 = document.get();
				document2 = modelMapper.map(dto, Document.class);
				
				String imagePath = IoUtil.updateDecoder(dto.getImagePath(),document2.getImagePath());
				
				String pdfPath = IoUtil.updateDecoder(dto.getPdfBase64(),document2.getPdfPath());
				
				document2.setSlug(SlugUtil.toSlug(document2.getNameTr()));
				

				return ResponseEntity.ok(new GeneralResponse(true,repo.save(document2),""));

			}else {
				return ResponseEntity.ok(new GeneralResponse(false,null,"Böyle bir döküman bulunamadı"));

			}
			
		} catch (Exception e) {
			
			String errorString = "DocsController on updateDocs "  +  e.getMessage();
			return ResponseEntity.ok(new GeneralResponse(false,null,errorString));
		}
		

	}
	
	@GetMapping("/delete")
	ResponseEntity<GeneralResponse> deleteDocs(@RequestHeader(value = "Authorization", required = false)String token,@RequestParam Long id){
		
		try {
			String aString = jwtHelper.verifyJwt(token);

			Optional<Document> document = repo.findById(id);
			if(document.isPresent()) {
				Document document2 = document.get();
				
				IoUtil.deleteFile(document2.getImagePath());
				IoUtil.deleteFile(document2.getPdfPath());
				repo.delete(document2);
				return ResponseEntity.ok(new GeneralResponse(true,null,""));

			}else {
				return ResponseEntity.ok(new GeneralResponse(false,null,"Böyle bir döküman bulunamadı"));

			}
			
		} catch (Exception e) {
			
			String errorString = "DocsController on deleteDocs "  +  e.getMessage();
			return ResponseEntity.ok(new GeneralResponse(false,null,errorString));
		}
		

	}
	
	@GetMapping("/get")
	ResponseEntity<GeneralResponse> getDoc(@RequestParam Long id){
		
		try {
			
			Optional<Document> document = repo.findById(id);
			if(document.isPresent()) {
				Document document2 = document.get();
				
				return ResponseEntity.ok(new GeneralResponse(true,document2,""));

			}else {
				return ResponseEntity.ok(new GeneralResponse(false,null,"Böyle bir döküman bulunamadı"));

			}
			
		} catch (Exception e) {
			
			String errorString = "DocsController on getDoc "  +  e.getMessage();
			return ResponseEntity.ok(new GeneralResponse(false,null,errorString));
		}
		

	}
	
	@GetMapping("/getSlug")
	ResponseEntity<GeneralResponse> getDocBySlug(@RequestParam String slug){
		
		try {
			
			Optional<List<Document>> document = Optional.ofNullable(repo.findBySlug(slug));
			if(document.isPresent()) {
				List<Document> document2 = document.get();
				
				return ResponseEntity.ok(new GeneralResponse(true,document2,""));

			}else {
				return ResponseEntity.ok(new GeneralResponse(false,null,"Böyle bir döküman bulunamadı"));

			}
			
		} catch (Exception e) {
			
			String errorString = "DocsController on getDoc "  +  e.getMessage();
			return ResponseEntity.ok(new GeneralResponse(false,null,errorString));
		}
		

	}
	

}
