package com.revolut.challenge.model;


import com.revolut.challenge.model.enumeration.TransferStatusType;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public final class Transfer implements Serializable {
    private Long id;
    private TransferStatusType transferStatusType;
    private Long fromAccount;
    private Long toAccount;
    private LocalDateTime createDatetime;
}
