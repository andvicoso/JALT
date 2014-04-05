package org.emast.view.ui.plot;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import net.ericaro.surfaceplotter.DefaultSurfaceModel;
import net.ericaro.surfaceplotter.JSurfacePanel;
import net.ericaro.surfaceplotter.surface.SurfaceModel;

/**
 *
 * @author andvicoso
 */
public class PlotFrame extends javax.swing.JFrame {

    private static final String DEFAULT_TITLE = "Gráfico de Superfície";
    private JSurfacePanel surfacePanel;

    public PlotFrame(SurfaceModel model) {
        this(model, DEFAULT_TITLE);
    }

    /**
     * Creates new form PlotFrame
     */
    public PlotFrame(SurfaceModel model, String title) {
        initComponents();
        setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        setSize(600, 400);

        surfacePanel = new JSurfacePanel(model);
        surfacePanel.setTitleText(title);
        add(surfacePanel, BorderLayout.CENTER);

        if (model instanceof DefaultSurfaceModel) {
            ((DefaultSurfaceModel) model).plot().execute();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this
     * code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
