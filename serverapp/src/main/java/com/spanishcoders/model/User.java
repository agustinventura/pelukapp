package com.spanishcoders.model;

import lombok.Data;

/**
 * Created by pep on 12/05/2016.
 */

@Data
public interface User {

    String getName();
    String getPassword();

}
