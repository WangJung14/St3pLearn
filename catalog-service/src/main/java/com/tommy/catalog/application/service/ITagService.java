package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.TagRequest;
import com.tommy.catalog.domain.entity.Tag;

import java.util.List;
import java.util.UUID;

public interface ITagService {
    List<Tag> getAllTags();
    Tag createTag(TagRequest request);
    Tag updateTag(UUID id, TagRequest request);
    void deleteTag(UUID id);
}
