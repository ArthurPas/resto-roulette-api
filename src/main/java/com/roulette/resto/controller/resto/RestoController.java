package com.roulette.resto.controller.resto;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.configuration.TrackCampaign;
import com.roulette.resto.data.common.dto.MediaResponse;
import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.resto.dto.in.NewBusinessHours;
import com.roulette.resto.data.resto.dto.in.NewFoodType;
import com.roulette.resto.data.resto.dto.in.NewRestaurant;
import com.roulette.resto.data.resto.dto.in.UpdateBusinessHours;
import com.roulette.resto.data.resto.dto.out.FoodTypeDto;
import com.roulette.resto.data.resto.dto.out.LabelDto;
import com.roulette.resto.data.resto.dto.out.RestoDto;
import com.roulette.resto.data.resto.entity.BusinessHour;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.data.resto.entity.VerificationStatus;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.exception.ErrorResponse;
import com.roulette.resto.service.administration.AdminService;
import com.roulette.resto.service.resto.RestoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

import static com.roulette.resto.service.common.MediaService.buildMediaUrl;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/restos")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class RestoController {

	final private RestoService restoService;
	private final AdminService adminService;
	private final JwtService jwtService;

	public RestoController(RestoService restoService, AdminService adminService, JwtService jwtService) {
		this.restoService = restoService;
		this.adminService = adminService;
		this.jwtService = jwtService;
	}

	@PostMapping("/create")
	@Tag(name = "Resto | Resto management")
	@Operation(summary = "Create a restaurant")
	public ResponseEntity<Restaurant> create(Authentication authentication, @RequestBody NewRestaurant newRestaurant) {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.createResto(newRestaurant),HttpStatus.OK);
	}
	@GetMapping("")
	@Tag(name = "Resto")
	@Operation(summary = "Get all restaurants")
	public ResponseEntity<List<RestoDto>> getAll(Authentication authentication, @RequestParam(required = false
			,defaultValue = "0") int page) {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.getRestos(page),HttpStatus.OK);
	}
	@PostMapping("/{id}/new-business-hours")
	@Tag(name = "Resto | Resto management")
	@Operation(summary = "Add opening and closing hours for resto", description = "With a resto id given in " +
			"parameter you can add a list of all the opening and closing hours by day. Weekday is an int between 1 " +
			"and 7 which represent the day of the week (eg: 1 for monday, 7 for sunday)")
	public ResponseEntity<List<BusinessHour>> newBusinessHours(Authentication authentication,
											  @RequestBody NewBusinessHours businessHours, @PathVariable String id) {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.addBusinessHoursToResto(businessHours, id),HttpStatus.CREATED);
	}
	@PatchMapping("/{id}/update-business-hours")
	@Tag(name = "Resto | Resto management")
	@Operation(summary = "update opening and closing hours for resto")
	public ResponseEntity<List<BusinessHour>> updateBusinessHours(Authentication authentication,
												 @RequestBody List<UpdateBusinessHours> newBusinessHours,
												 @PathVariable String id) {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.updateBusinessHours(newBusinessHours, id),HttpStatus.CREATED);
	}
	@GetMapping("/{id}")
	@Operation(summary = "Get a restaurant")
	@Tag(name = "Resto | Resto management")
	@TrackCampaign(type = "Resto")
	public ResponseEntity<RestoDto> getById(Authentication authentication, @PathVariable String id) {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.getRestoById(id),HttpStatus.OK);
	}
	@PutMapping("/{id}")
	@Operation(summary = "Modify restaurant infos")
	@Tag(name = "Resto | Resto management")
	public ResponseEntity<Restaurant> updateInfo(Authentication authentication, @PathVariable String id,
										@RequestBody NewRestaurant newRestaurant) {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.updateRestoInfoById(id,newRestaurant),HttpStatus.OK);
	}

	@PutMapping(path = "verify/{id}")
	@Tag(name = "Resto | Admin management")
	@Operation(summary = "Verify restaurant")
	public ResponseEntity<?> verifyResto(Authentication authentication,@PathVariable String id) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			restoService.verifyResto(id);
			record okResponse(VerificationStatus verificationStatus) {}
			return ResponseEntity.ok(new okResponse(VerificationStatus.VERIFIED));
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}

	}
	@PutMapping(path = "unverify/{id}")
	@Tag(name = "Resto | Admin management")
	@Operation(summary = "Remove verify status restaurant")
	public ResponseEntity<?> unverifyResto(Authentication authentication,@PathVariable String id) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			restoService.unverifyResto(id);
			record okResponse(VerificationStatus verificationStatus) {}
			return ResponseEntity.ok(new okResponse(VerificationStatus.UNVERIFIED));
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@PutMapping(path = "submitVerification/{id}")
	@Tag(name = "Resto | Admin management")
	@Operation(summary = "Submit verification request")
	public ResponseEntity<?> submitVerification(@PathVariable String id) {
		try {
			restoService.submitVerification(id);
			record okResponse(VerificationStatus verificationStatus) {}
			return ResponseEntity.ok(new okResponse(VerificationStatus.PENDING));
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@PutMapping(path = "rejectVerification/{id}")
	@Tag(name = "Resto | Admin management")
	@Operation(summary = "Reject verification request")
	public ResponseEntity<?> rejectVerification(Authentication authentication, @PathVariable String id) {
		adminService.rightCheckIsAdmin(authentication);
		restoService.submitVerification(id);
		record okResponse(VerificationStatus verificationStatus) {}
		return ResponseEntity.ok(new okResponse(VerificationStatus.REJECTED));
	}
	@GetMapping("/labels")
	@Tag(name = "Resto")
	@Operation(summary = "Get existing labels")
	public ResponseEntity<LabelDto> getAll(Authentication authentication) {
		return new ResponseEntity<>(new LabelDto(restoService.getLabels()),HttpStatus.OK);
	}


	@PostMapping("/food-types/create")
	@Operation(summary = "Add a new food type")
	@Tag(name = "Resto | Admin management")
	public ResponseEntity<NewFoodType> newFoodType(Authentication authentication, @RequestBody NewFoodType foodType) {
		adminService.rightCheckIsAdmin(authentication);
		return new ResponseEntity<>(restoService.createFoodType(foodType),HttpStatus.CREATED);
	}
	@GetMapping("/food-types")
	@Tag(name = "Resto")
	@Operation(summary = "Get all food types available")
	public ResponseEntity<FoodTypeDto> getAllFoodTypes(Authentication authentication) {
			return new ResponseEntity<>(new FoodTypeDto(restoService.foodTypeList()),HttpStatus.OK);
	}

	@PostMapping(path = "/{id}/upload-media", consumes = MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Add a new picture", description = """
			pictureType can be "menu","logo","resto" (which is any photo that the resto owner wants to display ...)
			""")
	@Tag(name = "Resto | Resto management")
	public ResponseEntity<MediaResource> addPicture(Authentication authentication,
											@RequestParam() MultipartFile menuPicture,
											@PathVariable String id, @RequestParam String pictureType) {
		adminService.rightCheckIsAdmin(authentication);
		MediaResource response = restoService.addPicture(id,menuPicture,pictureType);
		return new ResponseEntity<>(response,HttpStatus.CREATED);
	}
	@GetMapping(path = "/{id}/get-medias")
	@Operation(summary = "Get resto medias (menu pictures, logos ..) ")
	@Tag(name = "Resto")
	public ResponseEntity<List<MediaResponse>> getRestoMedias(@PathVariable String id) {

		List<MediaResource> pictures = restoService.getPictures(id);
		final List<MediaResponse> response = buildMediaUrl(pictures);

		return ResponseEntity.ok(response);
	}



	@DeleteMapping(path = "/{id}")
	@Tag(name = "Resto | Admin management")
	@Operation(summary = "Delete restaurant")
	public ResponseEntity<?> deleteRestaurant(Authentication authentication,@PathVariable String id) {
			adminService.rightCheckIsAdmin(authentication);
			restoService.deleteResto(id);
			record okResponse(String successMessage) {}
			return ResponseEntity.ok(new okResponse("resto deleted successfully"));

	}
	@DeleteMapping("/delete-media/{uuid}")
	@Tag(name = "Resto | Resto management")
	public ResponseEntity<Void> deleteMedia(@PathVariable String uuid) {
		restoService.removeMedia(uuid);
		return new ResponseEntity<>(HttpStatus.OK);
	}


}
