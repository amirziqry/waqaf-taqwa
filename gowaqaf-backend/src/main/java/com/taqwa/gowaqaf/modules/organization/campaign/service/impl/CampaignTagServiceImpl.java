package com.taqwa.gowaqaf.modules.organization.campaign.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.dto.CampaignTagDto;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.dto.CampaignTagUploadRequest;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.entity.CampaignTag;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.mapper.CampaignTagMapper;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.repository.CampaignTagRepository;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.service.CampaignTagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignTagServiceImpl implements CampaignTagService {

	private final CampaignTagRepository repository;

	private CampaignTag tryFindTagById(Long id) {
		return repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.CAT001, String.format("Tag %s not found.", id)));
	}

	@Override
	public CampaignTagDto createTag(CampaignTagUploadRequest dto) {
		CampaignTag tag = new CampaignTag();

		tag.setName(dto.name());

		CampaignTag saved = repository.save(tag);

		return CampaignTagMapper.mapTagToDto(saved);
	}

	@Override
	public CampaignTagDto updateTag(Long id, CampaignTagUploadRequest dto) {
		CampaignTag tag = tryFindTagById(id);

		tag.setName(dto.name());

		CampaignTag saved = repository.save(tag);

		return CampaignTagMapper.mapTagToDto(saved);
	}

	@Override
	public CampaignTag getTagById(Long id) {
		CampaignTag tag = tryFindTagById(id);

		return tag;
	}

	@Override
	public CampaignTagDto getTagDtoById(Long id) {
		CampaignTag tag = getTagById(id);

		return CampaignTagMapper.mapTagToDto(tag);
	}

	@Override
	public List<CampaignTagDto> getTagDtoList() {
		List<CampaignTag> categories = repository.findAll();

		List<CampaignTagDto> dtos = categories.stream().map(c -> CampaignTagMapper.mapTagToDto(c)).toList();

		return dtos;
	}

	@Override
	public List<CampaignTag> getAllTagById(Set<Long> ids) {
		List<CampaignTag> categories = repository.findAllById(ids);

		if (ids.size() != categories.size())
			throw new ResourceNotFoundException(ErrorCode.PRJ020, "Project Tag(s) not found");

		return categories;
	}

	@Override
	public void deleteTag(Long id) {
		if (!repository.existsById(id))
			throw new ResourceNotFoundException(ErrorCode.CAT001, String.format("Category %s not found.", id));

		repository.deleteById(id);
	}

}
