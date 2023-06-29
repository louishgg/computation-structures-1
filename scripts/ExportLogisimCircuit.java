import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.proj.Project;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import javax.imageio.ImageIO;

public final class ExportLogisimCircuit {
    private static final int BORDER = 24;

    private ExportLogisimCircuit() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3 || args.length > 6) {
            System.err.println("Usage: ExportLogisimCircuit <input.circ> <circuit-name> <output.png> [scale] [canvas|printer] [shaped|rectangular|din40700]");
            System.exit(2);
        }

        File input = new File(args[0]);
        String circuitName = args[1];
        File output = new File(args[2]);
        double scale = args.length >= 4 ? Double.parseDouble(args[3]) : 1.5;
        boolean printerView = args.length >= 5 && "printer".equals(args[4]);
        Object gateShape = parseGateShape(args.length == 6 ? args[5] : "shaped");

        Loader loader = new Loader(null);
        LogisimFile file = loader.openLogisimFile(input, Collections.emptyMap());
        Circuit circuit = file.getCircuit(circuitName);
        if (circuit == null) {
            throw new IllegalArgumentException("Circuit not found: " + circuitName);
        }

        Project project = new Project(file);
        CircuitState circuitState = project.getCircuitState(circuit);
        circuitState.getPropagator().propagate();

        Canvas canvas = new Canvas();
        BufferedImage measuringImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D measuringGraphics = measuringImage.createGraphics();
        measuringGraphics.setFont(new Font("SansSerif", Font.PLAIN, 12));
        Bounds bounds = circuit.getBounds(measuringGraphics).expand(BORDER);
        measuringGraphics.dispose();

        int width = Math.max(1, (int) Math.ceil(bounds.getWidth() * scale));
        int height = Math.max(1, (int) Math.ceil(bounds.getHeight() * scale));
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D base = image.createGraphics();
        base.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        base.setColor(Color.WHITE);
        base.fillRect(0, 0, width, height);
        base.setColor(Color.BLACK);

        Graphics2D drawing = (Graphics2D) base.create();
        drawing.setColor(Color.BLACK);
        drawing.scale(scale, scale);
        drawing.translate(-bounds.getX(), -bounds.getY());

        ComponentDrawContext context = new ExportDrawContext(canvas, circuit, circuitState, base, drawing, printerView, gateShape);
        context.setShowState(false);
        circuit.draw(context, null);

        drawing.dispose();
        base.dispose();

        File parent = output.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        ImageIO.write(image, "png", output);
        System.exit(0);
    }

    private static Object parseGateShape(String gateShape) {
        if ("shaped".equals(gateShape)) {
            return AppPreferences.SHAPE_SHAPED;
        } else if ("rectangular".equals(gateShape)) {
            return AppPreferences.SHAPE_RECTANGULAR;
        } else if ("din40700".equals(gateShape)) {
            return AppPreferences.SHAPE_DIN40700;
        } else {
            throw new IllegalArgumentException("Unsupported gate shape: " + gateShape);
        }
    }

    private static final class ExportDrawContext extends ComponentDrawContext {
        private final Object gateShape;

        private ExportDrawContext(Canvas dest, Circuit circuit, CircuitState circuitState,
                                  Graphics2D base, Graphics2D drawing, boolean printerView,
                                  Object gateShape) {
            super(dest, circuit, circuitState, base, drawing, printerView);
            this.gateShape = gateShape;
        }

        @Override
        public Object getGateShape() {
            return gateShape;
        }
    }
}
