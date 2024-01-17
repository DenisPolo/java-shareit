package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreationDto {

    private Long id;

    private Long ownerId;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemCreationDto that = (ItemCreationDto) o;
        return Objects.equals(id, that.id)
                && Objects.equals(ownerId, that.ownerId)
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(available, that.available)
                && Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, name, description, available, requestId);
    }
}