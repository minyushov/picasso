/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.picasso;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.widget.ImageView;

import static android.graphics.Color.WHITE;
import static com.squareup.picasso.Picasso.LoadedFrom.MEMORY;

final class PicassoDrawable extends Drawable {
  // Only accessed from main thread.
  private static final Paint DEBUG_PAINT = new Paint();
  private static final float FADE_DURATION = 200f; //ms

  /**
   * Create or update the drawable on the target {@link ImageView} to display the supplied bitmap
   * image.
   */
  static void setBitmap(ImageView target, Context context, Bitmap bitmap,
      Picasso.LoadedFrom loadedFrom, boolean noFade, boolean debugging,
      DrawableFactory drawableFactory) {
    Drawable placeholder = target.getDrawable();
    if (placeholder instanceof Animatable) {
      ((Animatable) placeholder).stop();
    }
    PicassoDrawable drawable =
        new PicassoDrawable(context, bitmap, placeholder, loadedFrom, noFade,
              debugging, drawableFactory);
    target.setImageDrawable(drawable);
  }

  /**
   * Create or update the drawable on the target {@link ImageView} to display the supplied
   * placeholder image.
   */
  static void setPlaceholder(ImageView target, Drawable placeholderDrawable) {
    target.setImageDrawable(placeholderDrawable);
    if (target.getDrawable() instanceof Animatable) {
      ((Animatable) target.getDrawable()).start();
    }
  }

  private final boolean debugging;
  private final float density;
  private final Picasso.LoadedFrom loadedFrom;
  final Drawable image;

  Drawable placeholder;

  long startTimeMillis;
  boolean animating;
  int alpha = 0xFF;

  PicassoDrawable(Context context, Bitmap bitmap, Drawable placeholder,
      Picasso.LoadedFrom loadedFrom, boolean noFade, boolean debugging,
      DrawableFactory drawableFactory) {
    Resources res = context.getResources();

    this.debugging = debugging;
    this.density = context.getResources().getDisplayMetrics().density;

    this.loadedFrom = loadedFrom;

    if (drawableFactory != null) {
      this.image = drawableFactory.createDrawable(bitmap);
    } else {
      this.image = new BitmapDrawable(res, bitmap);
    }

    boolean fade = loadedFrom != MEMORY && !noFade;
    if (fade) {
      this.placeholder = placeholder;
      animating = true;
      startTimeMillis = SystemClock.uptimeMillis();
    }
  }

  @Override public void draw(Canvas canvas) {
    if (!animating) {
      image.draw(canvas);
    } else {
      float normalized = (SystemClock.uptimeMillis() - startTimeMillis) / FADE_DURATION;
      if (normalized >= 1f) {
        animating = false;
        placeholder = null;
        image.draw(canvas);
      } else {
        if (placeholder != null) {
          placeholder.draw(canvas);
        }

        int partialAlpha = (int) (alpha * normalized);
        image.setAlpha(partialAlpha);
        image.draw(canvas);
        image.setAlpha(alpha);
        invalidateSelf();
      }
    }

    if (debugging) {
      drawDebugIndicator(canvas);
    }
  }

  @Override
  public int getIntrinsicWidth() {
    return image.getIntrinsicWidth();
  }

  @Override
  public int getIntrinsicHeight() {
    return image.getIntrinsicHeight();
  }

  @Override public void setAlpha(int alpha) {
    this.alpha = alpha;
    if (placeholder != null) {
      placeholder.setAlpha(alpha);
    }
    image.setAlpha(alpha);
  }

  @Override public void setColorFilter(ColorFilter cf) {
    if (placeholder != null) {
      placeholder.setColorFilter(cf);
    }
    image.setColorFilter(cf);
  }

  @Override
  public int getOpacity() {
    return image.getOpacity();
  }

  @Override protected void onBoundsChange(Rect bounds) {
    if (placeholder != null) {
      placeholder.setBounds(bounds);
    }
    image.setBounds(bounds);
  }

  private void drawDebugIndicator(Canvas canvas) {
    DEBUG_PAINT.setColor(WHITE);
    Path path = getTrianglePath(0, 0, (int) (16 * density));
    canvas.drawPath(path, DEBUG_PAINT);

    DEBUG_PAINT.setColor(loadedFrom.debugColor);
    path = getTrianglePath(0, 0, (int) (15 * density));
    canvas.drawPath(path, DEBUG_PAINT);
  }

  private static Path getTrianglePath(int x1, int y1, int width) {
    final Path path = new Path();
    path.moveTo(x1, y1);
    path.lineTo(x1 + width, y1);
    path.lineTo(x1, y1 + width);

    return path;
  }
}
