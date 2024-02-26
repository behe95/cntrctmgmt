package com.example.cntrctmgmt.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Converter(autoApply = true)
public class DatabaseDateTimeConverter implements AttributeConverter<LocalDateTime, String> {
    @Override
    public String convertToDatabaseColumn(LocalDateTime localDateTime) {
        // Define the date and time format expected by SQLite
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        // Format the current date and time
        return Objects.isNull(localDateTime) ? LocalDateTime.now().format(formatter) : localDateTime.format(formatter);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dateTimeString) {
        // Define the datetime formatter for the SQLite format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        // Parse the datetime string using the formatter
        return Objects.isNull(dateTimeString) ? null : LocalDateTime.parse(dateTimeString, formatter);
    }
}
