package org.emast.view.controller.plot;

import net.ericaro.surfaceplotter.DefaultSurfaceModel;
import net.ericaro.surfaceplotter.Mapper;
import net.ericaro.surfaceplotter.surface.AbstractSurfaceModel;
import net.ericaro.surfaceplotter.surface.SurfaceModel;
import org.emast.view.ui.plot.PlotFrame;

/**
 *
 * @author Anderson
 */
public class PlotDialogController {

    private PlotFrame plot;

    public void create() {
        SurfaceModel model = createModel();
        plot = new PlotFrame(model);
    }

    public void show() {
        plot.setVisible(true);
    }

    public SurfaceModel createModel() {
        AbstractSurfaceModel model = new DefaultSurfaceModel();
        model.setMapper(createDefaultMapper());

        return model;
    }

    public static void main(String[] args) {
        final PlotDialogController controller = new PlotDialogController();
        controller.create();
        controller.show();
    }

    protected Mapper createDefaultMapper() {
        return new Mapper() {
            @Override
            public float f1(float x, float y) {
                float r = x * x + y * y;

                if (r == 0) {
                    return 1f;
                }
                return (float) (Math.sin(r) / (r));
            }

            @Override
            public float f2(float x, float y) {
                return (float) (Math.sin(x * y));
            }
        };
    }
}
