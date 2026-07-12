package com.tommy.catalog.application.dto.request;

import java.util.List;
import java.util.UUID;

public record CourseBulkRequest(List<UUID> courseIds) {
}
