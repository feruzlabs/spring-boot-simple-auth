package dev.feruzlabs.springbootauth.utils;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@NoArgsConstructor
public class SortUtils {

    public static Sort buildSort(String sortBy, String sortDir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(direction, sortBy);
    }
}
