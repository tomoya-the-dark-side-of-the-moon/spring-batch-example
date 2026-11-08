package com.example.demo.domain;

import java.io.Serializable;

public record Reservation(Integer id, String fileName, String host, String userName, String dbName, String password)
		implements Serializable {
}
