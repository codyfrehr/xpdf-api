package io.xpdfutils.common;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ReadInputStreamTask implements Callable<String> {
    private final InputStream inputStream;

    @Override
    public String call() {
        val reader = new BufferedReader(new InputStreamReader(inputStream));
        val lines = reader.lines().collect(Collectors.joining("\n"));

        return StringUtils.isBlank(lines) ? null : lines;
    }
}
