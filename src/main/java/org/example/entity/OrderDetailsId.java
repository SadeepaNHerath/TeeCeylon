package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsId implements Serializable {
    private String ordId;
    private String proId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailsId that = (OrderDetailsId) o;
        return Objects.equals(ordId, that.ordId) && Objects.equals(proId, that.proId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordId, proId);
    }
}
