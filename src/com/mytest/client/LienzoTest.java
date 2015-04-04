package com.mytest.client;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.image.PictureLoadedHandler;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.types.NFastArrayList;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LienzoTest implements EntryPoint {

    final LienzoPanel lienzo = new LienzoPanel(1200,450);
    Picture pic;
    final Layer canvas = new Layer();
    private int defaultHeight = 200;
    private boolean zoomed;
    protected Double enlargeFactor = 1.1;

    public void onModuleLoad() {
        Layer backgroundGrid = getBackgroundGrid(lienzo.getWidth(), lienzo.getHeight());
        lienzo.add(backgroundGrid);

        lienzo.add(canvas);

        for (int i = 1; i < 7; i++) {
            pic = new Picture("/images/default("+i+").jpg");
            pic.setX(15 + 200 * i);
            pic.setID(Integer.toString(i));
            pic.setClippedImageDestinationHeight(defaultHeight);
            pic.setClippedImageDestinationWidth(defaultHeight);
            pic.onLoaded(new PictureLoadedHandler() {

                @Override
                public void onPictureLoaded(Picture picture) {
                    canvas.draw();
                }

            });
            canvas.add(pic);

        }

        enlargeVersion();
        RootPanel.get().add(lienzo);

    }

    private void enlargeVersion() {

        NFastArrayList<IPrimitive<?>> childNodes = canvas.getChildNodes();

        for (int i = 0; i < childNodes.size(); i++) {

            if (childNodes.get(i) instanceof Picture) {
                Picture picture = (Picture) childNodes.get(i);
                picture.addNodeMouseClickHandler(addNodeMouseClickHandler(picture));
                picture.addNodeMouseDoubleClickHandler(addNodeMouseDoubleClickHandler(picture));
            }

        }

    }

    private NodeMouseDoubleClickHandler addNodeMouseDoubleClickHandler(final Picture picture) {

        return new NodeMouseDoubleClickHandler() {
            @Override
            public void onNodeMouseDoubleClick(NodeMouseDoubleClickEvent event) {
                if (isZoomed()) {
                    
                    enlargeFactor = getEnlargeFactor(picture.getClippedImageDestinationHeight());
                    enlargeChildren(canvas,
                            (double) ((defaultHeight + 0.0) / picture.getClippedImageDestinationHeight()));
                    setZoomed(false);
                    ConLog.console(enlargeFactor+" NotZoomed");
                    // resetZoom();

                } else {
                    setZoomed(true);
                    enlargeFactor = getEnlargeFactor(picture.getClippedImageDestinationHeight());
                    enlargeChildren(canvas, enlargeFactor);
                    ConLog.console(enlargeFactor+"");
                    center(picture);
                }

                canvas.draw();
            }
        };
    }

    private NodeMouseClickHandler addNodeMouseClickHandler(final Picture picture) {

        return new NodeMouseClickHandler() {

            @Override
            public void onNodeMouseClick(NodeMouseClickEvent event) {

                if (isZoomed()) {
                    center(picture);
                    canvas.draw();
                }

            }
        };
    }

    protected boolean isZoomed() {

        return zoomed;
    }

    protected void setZoomed(boolean b) {
        zoomed = b;

    }

    protected Double getEnlargeFactor(int heightNow) {
        // Number large enough that nearby boxes would be seen
        double constant = 2.0;
        double newHeight = heightNow * constant;

        return newHeight / heightNow;
    }

    private void enlargeChildren(Layer canvas, Double enlargeBy) {
        NFastArrayList<IPrimitive<?>> childNodes = canvas.getChildNodes();

        Picture picture = null;

        for (int i = 0; i < childNodes.size(); i++) {

            if (childNodes.get(i) instanceof Picture) {

                picture = (Picture) childNodes.get(i);
                enlarge(picture, enlargeBy);
                moveChild(picture, enlargeBy);
            }
        }
    }

    private void enlarge(Picture picture, Double enlargeBy) {
        int dimentsions = defaultHeight;
        if (enlargeBy > 0) {
            dimentsions = (int) (picture.getClippedImageDestinationHeight() * enlargeBy);
        }
        setWitdhHeight(picture, dimentsions);

    }

    private void setWitdhHeight(Picture pic, int dimentsions) {

        pic.setClippedImageDestinationWidth(dimentsions);
        pic.setClippedImageDestinationHeight(dimentsions);

    }

    private void center(Picture picture) {
        int lienzoXCenter = lienzo.getWidth() / 2;
        int lienzoYCenter = lienzo.getHeight() / 2;

        Double centerImageX = (double) (lienzoXCenter - (picture.getClippedImageDestinationWidth() / 2));
        Double xMove = centerImageX - picture.getX();

        Double centerImageY = (double) (lienzoYCenter - (picture.getClippedImageDestinationHeight() / 2));
        Double yMove = centerImageY - picture.getY();

        moveChildren(canvas, xMove, yMove);

    }

    private void moveChildren(Layer canvas, Double xMove, Double yMove) {

        NFastArrayList<IPrimitive<?>> childNodes = canvas.getChildNodes();

        for (int i = 0; i < childNodes.size(); i++) {
            if (childNodes.get(i) instanceof Picture) {
                moveChild((Picture) childNodes.get(i), xMove, yMove);
            }
        }
    }

    private void moveChild(Picture picture, double xMove, double yMove) {
        double x = picture.getX() + xMove;
        double y = picture.getY() + yMove;
        setXAndY(picture, x, y);

    }

    private void moveChild(Picture picture, double movePercent) {
        double x = picture.getX() * movePercent;
        double y = picture.getY() * movePercent;
        setXAndY(picture, x, y);

    }

    private void setXAndY(Picture picture, double x, double y) {
        picture.setX(x);
        picture.setY(y);

    }

    public void addLayerHandler() {
        canvas.addNodeMouseDoubleClickHandler(new NodeMouseDoubleClickHandler() {

            public void onNodeMouseDoubleClick(NodeMouseDoubleClickEvent event) {

                // resetZoom();
            }
        });

    }

    private Layer getBackgroundGrid(final int wide, final int high) {
        final int stepx = 10;
        final int stepy = 10;
        final String color = "#CEF6F5";
        final double lineWidth = 0.8;

        final Layer grid = new Layer() {
            @Override
            public void draw() {
                Context2D context = getContext();
                context.setStrokeColor(color);
                context.setStrokeWidth(lineWidth);

                for (double i = stepx + lineWidth; i < wide; i += stepx) {
                    context.beginPath();
                    context.moveTo(i, 0);
                    context.lineTo(i, high);
                    context.stroke();
                }

                for (double i = stepy + lineWidth; i < high; i += stepy) {
                    context.beginPath();
                    context.moveTo(0, i);
                    context.lineTo(wide, i);
                    context.stroke();
                }
            }
        };
        // we are not interested in any events on this layer
        grid.setListening(false);

        return grid;
    }

}