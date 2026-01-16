package com.roulette.resto.controller.resto;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.data.common.dto.MediaResponse;
import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.resto.dto.in.NewBusinessHours;
import com.roulette.resto.data.resto.dto.in.NewFoodType;
import com.roulette.resto.data.resto.dto.in.NewRestaurant;
import com.roulette.resto.data.resto.dto.in.UpdateBusinessHours;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Restaurant info",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Restaurant.class)))})
	public ResponseEntity<?> create(Authentication authentication, @RequestBody NewRestaurant newRestaurant) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.createResto(newRestaurant),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@GetMapping("")
	@Tag(name = "Resto")
	@Operation(summary = "Get all restaurants")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Restaurant info",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = List.class)))})
	public ResponseEntity<?> getAll(Authentication authentication, @RequestParam(required = false
			,defaultValue = "0") int page) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.getRestos(page),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@PostMapping("/{id}/new-business-hours")
	@Tag(name = "Resto | Resto management")
	@Operation(summary = "Add opening and closing hours for resto", description = "With a resto id given in " +
			"parameter you can add a list of all the opening and closing hours by day. Weekday is an int between 1 " +
			"and 7 which represent the day of the week (eg: 1 for monday, 7 for sunday)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = BusinessHour.class)))})
	public ResponseEntity<?> newBusinessHours(Authentication authentication,
											  @RequestBody NewBusinessHours businessHours, @PathVariable String id) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.addBusinessHoursToResto(businessHours, id),HttpStatus.CREATED);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@PatchMapping("/{id}/update-business-hours")
	@Tag(name = "Resto | Resto management")
	@Operation(summary = "update opening and closing hours for resto")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = BusinessHour.class)))})
	public ResponseEntity<?> updateBusinessHours(Authentication authentication,
												 @RequestBody List<UpdateBusinessHours> newBusinessHours,
												 @PathVariable String id) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.updateBusinessHours(newBusinessHours, id),HttpStatus.CREATED);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@GetMapping("/{id}")
	@Operation(summary = "Get a restaurant")
	@Tag(name = "Resto | Resto management")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Restaurant info",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Restaurant.class)))})
	public ResponseEntity<?> getById(Authentication authentication, @PathVariable String id) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.getRestoById(id),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@PutMapping("/{id}")
	@Operation(summary = "Modify restaurant infos")
	@Tag(name = "Resto | Resto management")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Restaurant info",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Restaurant.class)))})
	public ResponseEntity<?> updateInfo(Authentication authentication, @PathVariable String id,
										@RequestBody NewRestaurant newRestaurant) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.updateRestoInfoById(id,newRestaurant),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}

	@PutMapping(path = "verify/{id}")
	@Tag(name = "Resto | Admin management")
	@Operation(summary = "Verify restaurant")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Integer.class)))})
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
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Integer.class)))})
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
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Integer.class)))})
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
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Integer.class)))})
	public ResponseEntity<?> rejectVerification(Authentication authentication, @PathVariable String id) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			restoService.submitVerification(id);
			record okResponse(VerificationStatus verificationStatus) {}
			return ResponseEntity.ok(new okResponse(VerificationStatus.REJECTED));
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@GetMapping("/labels")
	@Tag(name = "Resto")
	@Operation(summary = "Get existing labels")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Label",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = List.class)))})
	public ResponseEntity<?> getAll(Authentication authentication) {
		try {
			return new ResponseEntity<>(restoService.getLabels(),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}


	@PostMapping("/food-types/create")
	@Operation(summary = "Add a new food type")
	@Tag(name = "Resto | Admin management")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Void.class)))})
	public ResponseEntity<?> newFoodType(Authentication authentication, @RequestBody NewFoodType foodType) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.createFoodType(foodType),HttpStatus.CREATED);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@GetMapping("/food-types")
	@Tag(name = "Resto")
	@Operation(summary = "Get all food types available")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = List.class)))})
	public ResponseEntity<?> getAllFoodTypes(Authentication authentication) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.foodTypeList(),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}

	@PostMapping(path = "/{id}/upload-media", consumes = MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Add a new picture", description = """
			pictureType can be "menu","logo","resto" (which is any photo that the resto owner wants to display ...)
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Integer.class)))})
	@Tag(name = "Resto | Resto management")
	public ResponseEntity<?> addPicture(Authentication authentication,
											@RequestParam() MultipartFile menuPicture,
											@PathVariable String id, @RequestParam String pictureType) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			MediaResource response = restoService.addPicture(id,menuPicture,pictureType);
			return new ResponseEntity<>(response,HttpStatus.CREATED);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@GetMapping(path = "/{id}/get-medias")
	@Operation(summary = "Get resto medias (menu pictures, logos ..) ")
	@Tag(name = "Resto")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Integer.class)))})
	public ResponseEntity<?> getRestoMedias(@PathVariable String id) {

		List<MediaResource> pictures = restoService.getPictures(id);
		final List<MediaResponse> response = buildMediaUrl(pictures);

		return ResponseEntity.ok(response);
	}



	@DeleteMapping(path = "/{id}")
	@Tag(name = "Resto | Admin management")
	@Operation(summary = "Delete restaurant")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Integer.class)))})
	public ResponseEntity<?> deleteRestaurant(Authentication authentication,@PathVariable String id) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			restoService.deleteResto(id);
			record okResponse(String successMessage) {}
			return ResponseEntity.ok(new okResponse("resto deleted successfully"));
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}

	}
	@DeleteMapping("/delete-media/{uuid}")
	@Tag(name = "Resto | Resto management")
	public ResponseEntity<Void> deleteMedia(@PathVariable String uuid) {
		restoService.removeMedia(uuid);
		return new ResponseEntity<>(HttpStatus.OK);
	}


}
