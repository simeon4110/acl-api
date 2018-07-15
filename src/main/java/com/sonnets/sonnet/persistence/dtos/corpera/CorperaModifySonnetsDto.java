package com.sonnets.sonnet.persistence.dtos.corpera;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;

/**
 * POJO for adding/removing sonnets to/from a corpera.
 *
 * @author Josh Harkema
 */
public class CorperaModifySonnetsDto {
    @NotEmpty
    private String corperaId;
    @NotEmpty
    private String[] sonnetId;

    public String getCorperaId() {
        return corperaId;
    }

    public void setCorperaId(String corperaId) {
        this.corperaId = corperaId;
    }

    public String[] getSonnetId() {
        return sonnetId;
    }

    public void setSonnetId(String[] sonnetId) {
        this.sonnetId = sonnetId;
    }

    @Override
    public String toString() {
        return "CorperaModifySonnetsDto{" +
                "corperaId=" + corperaId +
                ", sonnetId=" + Arrays.toString(sonnetId) +
                '}';
    }

}
