package br.com.fiap.petcare360_java.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.petcare360_java.dto.VeterinarianLinkRequest;
import br.com.fiap.petcare360_java.dto.VeterinarianLinkResponse;
import br.com.fiap.petcare360_java.exception.ApiException;
import br.com.fiap.petcare360_java.model.AppUser;
import br.com.fiap.petcare360_java.model.Pet;
import br.com.fiap.petcare360_java.model.PetVeterinarian;
import br.com.fiap.petcare360_java.repository.PetVeterinarianRepository;

@Service
public class VeterinarianLinkService {

	private final CurrentUserService currentUserService;
	private final ClinicalAccessService clinicalAccessService;
	private final PetVeterinarianRepository petVeterinarianRepository;

	public VeterinarianLinkService(CurrentUserService currentUserService, ClinicalAccessService clinicalAccessService,
			PetVeterinarianRepository petVeterinarianRepository) {
		this.currentUserService = currentUserService;
		this.clinicalAccessService = clinicalAccessService;
		this.petVeterinarianRepository = petVeterinarianRepository;
	}

	@Transactional
	public VeterinarianLinkResponse link(VeterinarianLinkRequest request) {
		Pet pet = clinicalAccessService.accessiblePet(request.petId());
		AppUser tutor = currentUserService.user();
		if (!pet.getUser().getId().equals(tutor.getId()) && !currentUserService.hasRole("ROLE_ADMIN")) {
			throw new ApiException(HttpStatus.FORBIDDEN, "Apenas o tutor pode vincular veterinário ao pet");
		}

		AppUser veterinarian = clinicalAccessService.userById(request.veterinarianId());
		boolean isVet = veterinarian.getRoles().stream()
				.anyMatch(role -> "ROLE_VETERINARIO".equals(role.getName()));
		if (!isVet) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Usuário selecionado não possui perfil de veterinário");
		}

		if (petVeterinarianRepository.existsActiveLink(pet.getId(), veterinarian.getEmail())) {
			throw new ApiException(HttpStatus.CONFLICT, "Veterinário já vinculado a este pet");
		}

		PetVeterinarian link = new PetVeterinarian();
		link.setPet(pet);
		link.setVeterinarian(veterinarian);
		link.setActive(true);
		PetVeterinarian saved = petVeterinarianRepository.save(link);
		return toResponse(saved);
	}

	private VeterinarianLinkResponse toResponse(PetVeterinarian link) {
		return new VeterinarianLinkResponse(
				link.getPet().getId(),
				link.getPet().getName(),
				link.getVeterinarian().getId(),
				link.getVeterinarian().getName(),
				link.getAssignedAt(),
				link.getActive());
	}
}
