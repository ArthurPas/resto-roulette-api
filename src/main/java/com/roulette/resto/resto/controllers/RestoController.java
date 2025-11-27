package com.roulette.resto.resto.controllers;

import com.roulette.resto.administration.services.AdminService;
import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.common.exception.ErrorResponse;
import com.roulette.resto.resto.dto.MenuPicture;
import com.roulette.resto.resto.dto.in.*;
import com.roulette.resto.resto.entity.BusinessHour;
import com.roulette.resto.resto.entity.Label;
import com.roulette.resto.resto.entity.Restaurant;
import com.roulette.resto.resto.services.RestoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/restos")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class RestoController {

	final private RestoService restoService;
	private final AdminService adminService;

	public RestoController(RestoService restoService, AdminService adminService) {
		this.restoService = restoService;
		this.adminService = adminService;
	}

	@PostMapping("/create")
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
	@Operation(summary = "Get all restaurants")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Restaurant info",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = List.class)))})
	public ResponseEntity<?> getAll(Authentication authentication, @RequestParam(required = false
			,defaultValue = "0") int page) {
		try {
			log.error("coucou");
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.getRestos(page),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@PostMapping("/{id}/new-business-hours")
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
	@Operation(summary = "update opening and closing hours for resto")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = BusinessHour.class)))})
	public ResponseEntity<?> updateBusinessHours(Authentication authentication,
												 @RequestBody UpdateBusinessHours newBusinessHours,
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
	@PatchMapping("/{id}")
	@Operation(summary = "Modify restaurant infos")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Restaurant info",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Restaurant.class)))})
	public ResponseEntity<?> updateInfo(Authentication authentication, @PathVariable String id,
										@RequestBody NewRestaurant newRestaurant) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			log.warn(newRestaurant.toString());
			return new ResponseEntity<>(restoService.updateRestoInfoById(id,newRestaurant),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@PostMapping("/labels/admin/new-label")
	@Operation(summary = "Add a label in database")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Label",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Restaurant.class)))})
	public ResponseEntity<?> newLabel(Authentication authentication, @RequestBody Label label ) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.addNewLabel(label),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@PostMapping("/{id}/labels/new-label")
	@Operation(summary = "Add a labels for restaurant")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Labels",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = List.class)))})
	public ResponseEntity<?> newLabel(Authentication authentication, @RequestBody AddLabels labels,
									  @PathVariable String id) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.addLabelsResto(labels, id),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
	@GetMapping("/labels")
	@Operation(summary = "Get existing labels")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Label",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = List.class)))})
	public ResponseEntity<?> getAll(Authentication authentication) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(restoService.getLabels(),HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}


	@PostMapping("/food-types/create")
	@Operation(summary = "Add a new food type")
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

	@PostMapping(path = "/{id}/upload-menu-pictures", consumes = MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Add a new picture for the menu")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Integer.class)))})
	public ResponseEntity<?> addMenuPicture(Authentication authentication,
											@RequestParam() MultipartFile menuPicture,
											@PathVariable String id) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			String resourceId = restoService.addMenuPicture(id,menuPicture);
			Map<String, String> response = new HashMap<>();
			response.put("resourceId",resourceId);
			return new ResponseEntity<>(response,HttpStatus.CREATED);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}

	@PostMapping(path = "/{id}/get-menu-pictures")
	@Operation(summary = "Get resto menu pictures ")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Success",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = Integer.class)))})
	public ResponseEntity<?> getMenuPictures(Authentication authentication,@PathVariable String id) {

		record PictureResponseDto(String type, String url) {}
		List<MenuPicture> pictures = restoService.getMenusByRestoId(id);
		List<PictureResponseDto> response = pictures.stream()
				.map(pic -> {
					String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
							.path("/images/")
							.path(pic.getUuid())
							.toUriString();

					return new PictureResponseDto("MENU",downloadUrl);
				})
				.collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}
	@DeleteMapping(path = "/{id}")
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

}
