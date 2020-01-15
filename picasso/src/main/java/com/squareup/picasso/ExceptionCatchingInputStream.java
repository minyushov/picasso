package com.squareup.picasso;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * An {@link java.io.InputStream} that catches {@link java.io.IOException}s and stores them
 * so they can be handled later. This is used to workaround a framework issue
 * where exceptions are swallowed in {@link android.graphics.BitmapFactory}. As a result
 * {@link android.graphics.BitmapFactory} can return partially decoded bitmaps.
 */
class ExceptionCatchingInputStream extends InputStream {
  @NonNull
  private final InputStream stream;
  @Nullable
  private IOException exception;

  ExceptionCatchingInputStream(@NonNull InputStream stream) {
    this.stream = stream;
  }

  @Nullable
  public IOException getException() {
    return exception;
  }

  @Override
  public int available() throws IOException {
    return stream.available();
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }

  @Override
  public synchronized void mark(int readlimit) {
    stream.mark(readlimit);
  }

  @Override
  public boolean markSupported() {
    return stream.markSupported();
  }

  @Override
  public int read() {
    try {
      return stream.read();
    } catch (IOException exception) {
      this.exception = exception;
      return -1;
    }
  }

  @Override
  public int read(@NonNull byte[] b) {
    try {
      return stream.read(b);
    } catch (IOException exception) {
      this.exception = exception;
      return -1;
    }
  }

  @Override
  public int read(@NonNull byte[] b, int off, int len) {
    try {
      return stream.read(b, off, len);
    } catch (IOException exception) {
      this.exception = exception;
      return -1;
    }
  }

  @Override
  public long skip(long n) {
    try {
      return stream.skip(n);
    } catch (IOException exception) {
      this.exception = exception;
      return -1;
    }
  }

  @Override
  public synchronized void reset() throws IOException {
    stream.reset();
  }
}