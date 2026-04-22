package com.Nulances.dto.response;

import lombok.Builder;

@Builder
public record DashboardStatsMarketplaceResponse(
        long totalAnuncios,
        long totalPublicados,
        long totalPendentes,
        long totalSuspensos
) {
}
