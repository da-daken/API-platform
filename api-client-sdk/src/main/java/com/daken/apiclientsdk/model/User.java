package com.daken.apiclientsdk.model;


import lombok.Data;

import java.io.Serializable;


@Data
public class User implements Serializable {

    private String username;

    private static final long serialVersionUID = 1L;

}
