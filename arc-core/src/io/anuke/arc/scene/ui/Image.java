package io.anuke.arc.scene.ui;

import io.anuke.arc.graphics.Texture;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.NinePatch;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.geom.Vector2;
import io.anuke.arc.scene.Element;
import io.anuke.arc.scene.Skin;
import io.anuke.arc.scene.style.Drawable;
import io.anuke.arc.scene.style.NinePatchDrawable;
import io.anuke.arc.scene.style.TextureRegionDrawable;
import io.anuke.arc.scene.style.TransformDrawable;
import io.anuke.arc.util.Align;
import io.anuke.arc.util.Scaling;

import static io.anuke.arc.Core.scene;

/**
 * Displays a {@link Drawable}, scaled various way within the widgets bounds. The preferred size is the min size of the drawable.
 * Only when using a {@link TextureRegionDrawable} will the actor's scale, rotation, and origin be used when drawing.
 * @author Nathan Sweet
 */
public class Image extends Element{
    protected float imageX, imageY, imageWidth, imageHeight;
    private Scaling scaling;
    private int align;
    private Drawable drawable;

    /** Creates an image with no region or patch, stretched, and aligned center. */
    public Image(){
        this((Drawable)null);
    }

    public Image(String name){
        this(scene.skin.getDrawable(name));
    }

    /**
     * Creates an image stretched, and aligned center.
     * @param patch May be null.
     */
    public Image(NinePatch patch){
        this(new NinePatchDrawable(patch), Scaling.stretch, Align.center);
    }

    /**
     * Creates an image stretched, and aligned center.
     * @param region May be null.
     */
    public Image(TextureRegion region){
        this(new TextureRegionDrawable(region), Scaling.stretch, Align.center);
    }

    /** Creates an image stretched, and aligned center. */
    public Image(Texture texture){
        this(new TextureRegionDrawable(new TextureRegion(texture)));
    }

    /** Creates an image stretched, and aligned center. */
    public Image(Skin skin, String drawableName){
        this(skin.getDrawable(drawableName), Scaling.stretch, Align.center);
    }

    /**
     * Creates an image stretched, and aligned center.
     * @param drawable May be null.
     */
    public Image(Drawable drawable){
        this(drawable, Scaling.stretch, Align.center);
    }

    /**
     * Creates an image aligned center.
     * @param drawable May be null.
     */
    public Image(Drawable drawable, Scaling scaling){
        this(drawable, scaling, Align.center);
    }

    /** @param drawable May be null. */
    public Image(Drawable drawable, Scaling scaling, int align){
        setDrawable(drawable);
        this.scaling = scaling;
        this.align = align;
        setSize(getPrefWidth(), getPrefHeight());
    }

    public void layout(){
        if(drawable == null) return;

        float regionWidth = drawable.getMinWidth();
        float regionHeight = drawable.getMinHeight();
        float width = getWidth();
        float height = getHeight();

        Vector2 size = scaling.apply(regionWidth, regionHeight, width, height);
        imageWidth = size.x;
        imageHeight = size.y;

        if((align & Align.left) != 0)
            imageX = 0;
        else if((align & Align.right) != 0)
            imageX = (int)(width - imageWidth);
        else
            imageX = (int)(width / 2 - imageWidth / 2);

        if((align & Align.top) != 0)
            imageY = (int)(height - imageHeight);
        else if((align & Align.bottom) != 0)
            imageY = 0;
        else
            imageY = (int)(height / 2 - imageHeight / 2);
    }

    public void draw(){
        validate();

        float x = getX();
        float y = getY();
        float scaleX = getScaleX();
        float scaleY = getScaleY();
        Draw.color(getColor());
        Draw.alpha(parentAlpha * getColor().a);

        if(drawable instanceof TransformDrawable){
            float rotation = getRotation();
            if(scaleX != 1 || scaleY != 1 || rotation != 0){
                drawable.draw(x + imageX, y + imageY, getOriginX() - imageX, getOriginY() - imageY,
                imageWidth, imageHeight, scaleX, scaleY, rotation);
                return;
            }
        }
        if(drawable != null) drawable.draw(x + imageX, y + imageY, imageWidth * scaleX, imageHeight * scaleY);
    }

    /** @return May be null. */
    public Drawable getDrawable(){
        return drawable;
    }

    public void setDrawable(TextureRegion region){
        setDrawable(new TextureRegionDrawable(region));
    }

    public void setDrawable(String drawableName){
        setDrawable(scene.skin.getDrawable(drawableName));
    }

    /** @param drawable May be null. */
    public void setDrawable(Drawable drawable){
        if(this.drawable == drawable) return;
        if(drawable != null){
            if(getPrefWidth() != drawable.getMinWidth() || getPrefHeight() != drawable.getMinHeight())
                invalidateHierarchy();
        }else
            invalidateHierarchy();
        this.drawable = drawable;
    }

    public Image setScaling(Scaling scaling){
        if(scaling == null) throw new IllegalArgumentException("scaling cannot be null.");
        this.scaling = scaling;
        invalidate();

        return this;
    }

    public void setAlign(int align){
        this.align = align;
        invalidate();
    }

    public float getMinWidth(){
        return 0;
    }

    public float getMinHeight(){
        return 0;
    }

    public float getPrefWidth(){
        if(drawable != null) return drawable.getMinWidth();
        return 0;
    }

    public float getPrefHeight(){
        if(drawable != null) return drawable.getMinHeight();
        return 0;
    }

    public float getImageX(){
        return imageX;
    }

    public float getImageY(){
        return imageY;
    }

    public float getImageWidth(){
        return imageWidth;
    }

    public float getImageHeight(){
        return imageHeight;
    }
}