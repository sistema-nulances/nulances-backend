package com.Nulances.controller.admin;

import com.Nulances.dto.request.BannerCreateRequest;
import com.Nulances.dto.request.BannerUpdateRequest;
import com.Nulances.dto.request.BannerUploadRequest;
import com.Nulances.dto.response.BannerAdminResponse;
import com.Nulances.dto.response.BannerUploadResponse;
import com.Nulances.service.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload-url")
    public BannerUploadResponse gerarUploadUrl(@Valid @RequestBody BannerUploadRequest request) {
        return bannerService.gerarUploadUrl(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public BannerAdminResponse criar(@Valid @RequestBody BannerCreateRequest request) {
        return bannerService.criar(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<BannerAdminResponse> listar() {
        return bannerService.listarAdmin();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public BannerAdminResponse buscarPorId(@PathVariable UUID id) {
        return bannerService.buscarPorIdAdmin(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public BannerAdminResponse editar(@PathVariable UUID id, @Valid @RequestBody BannerUpdateRequest request) {
        return bannerService.editar(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable UUID id) {
        bannerService.excluir(id);
    }
}