package io.xpdftools.common.util;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * A task that converts an {@code InputStream} connected to a {@code Process} to a {@code String}.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@RequiredArgsConstructor
public class ReadInputStreamTask implements Callable<String> {

    /**
     * The {@code InputStream} connected to a {@code Process}.
     */
    private final InputStream inputStream;

    //todo: do line-endings need to be considered..?
    //      maybe there is a way to write this so that lines join using default system line-ending property (look up java property for that)
    @Override
    public String call() {
        val reader = new BufferedReader(new InputStreamReader(inputStream));
        val lines = reader.lines().collect(Collectors.joining("\n"));

        return StringUtils.isBlank(lines) ? null : lines;
    }

}
