package com.tommy.catalog.application.service.serviceimpl;

import com.tommy.catalog.application.dto.request.TagRequest;

import com.tommy.catalog.application.service.ITagService;
import com.tommy.catalog.domain.entity.Tag;
import com.tommy.catalog.domain.exception.AppException;
import com.tommy.catalog.domain.exception.ErrorCode;
import com.tommy.catalog.infrastructure.persistence.repository.TagRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService implements ITagService {

    private final TagRepository tagRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    @Transactional
    public Tag createTag(TagRequest request) {
        Tag tag = Tag.builder().name(request.getName().toUpperCase()).build();
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag updateTag(UUID id, TagRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        tag.setName(request.getName().toUpperCase());
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public void deleteTag(UUID id) {
        if (!tagRepository.existsById(id)) {
            throw new AppException(ErrorCode.TAG_NOT_FOUND);
        }
        tagRepository.deleteById(id);
    }
}