package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.core.TextContent;
import com.storedobject.helper.ID;
import com.storedobject.helper.LitComponent;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <p>Component for drawing flow diagrams.</p>
 * <p>Usage example (Inheritance):</p>
 * <pre>
 * #background: #ffffff
 * #zoom: 1
 * #gutter: 10
 * #edgeMargin: 5
 * #padding: 10
 * [Shape | String name | String getName(); void setName(String); double getArea()]
 * [Rectangle | double width; double height | double getWidth(); void setWidth(double); double getHeight(); void setHeight(double)]
 * [Square | | double getSide(); void setSide(double side)]
 * [Circle | double radius | double getRadius(); void setRadius(double)]
 * [Shape] &lt;:- [Rectangle]
 * [Rectangle] &lt;:- [Square]
 * [Shape] &lt;:- [Circle]
 * </pre>
 * <p>Usage example (Dependency):</p>
 *<pre>
 * [Person | boolean hasRead(Book book)]
 * [Book]
 * [Person] has read --&gt; [Book]
 *</pre>
 * <p>Usage example (Realization - implementation of an interface):</p>
 * <pre>
 * [HasSpeed | double getSpeed()]
 * [Car | double getSpeed()]
 * [Aircraft | double getSpeed()]
 * [HasSpeed] &lt;:-- [Car]
 * [HasSpeed] &lt;:-- [Aircraft]
 * </pre>
 * <p>Usage example: Order System</p>
 * <pre>
 * #direction: right
 * [Customer | String name; String address]
 * [Order | Date date; int status]
 * [Order Detail | double quantity]
 * [Item | String description]
 * [Payment | double amount]
 * [Cash Payment]
 * [Bank Payment | String customerID, String bankID ]
 * [Credit Sale | String customerID, Date dueDate]
 * [Customer] - 0..* [Order]
 * [Order] line item o- 1..* [Order Detail]
 * [Order Detail] 0..* -&gt; 1 [Item]
 * [Order] 1 - 1..* [Payment]
 * [Payment] &lt;:- [Cash Payment]
 * [Payment] &lt;:- [Bank Payment]
 * [Payment] &lt;:- [Credit Sale]
 * </pre>
 * <p>Usage (Miscellaneous):</p>
 * <pre>
 * Car has an Engine
 * [Car] -&gt; [Engine]
 *
 * Car owns an arbitrary number of blemished
 * [Car] +-&gt; 0..* [RustPatches]
 *
 * Car knows its Manufacturer
 * [Car] o-&gt; [Manufacturer]
 *
 * Car depends on Fuel
 * [Car] --&gt; [Fuel]
 *
 * Pickup inherits from Car
 * [Pickup] -:&gt; [Car]
 *
 * Car implements interface IVehicle
 * [Car] --:&gt; [IVehicle]
 *
 * Paul and Ron have a named association
 * [Paul] friend - [Ron]
 *
 * They both depend on each other
 * [Chicken] &lt;-&gt; [Egg]
 *
 * Car has some attributes
 * [Car | maxSpeed: Float;color: Color]
 *
 * Car has several valves
 * [Car | valves: Valve\[\] ]
 *
 * Engine has an operation
 * [Engine | | start()]
 *
 * Engine has internal parts
 * [Engine |
 *   [Cylinder] -&gt; 1 [Piston]
 *   [Cylinder] -&gt; 2 [Valve]
 * ]
 *
 * Engine is an abstract class
 * [&lt;abstract&gt;Engine | | start()]
 *
 * Car is in the package 'vehicles'
 * [&lt;package&gt;vehicles | [Car]]
 *
 * Car has an attached note
 * [Car] -- [&lt;note&gt; only driven twice a month]
 *
 * Set font
 * #font: Times
 * Layout the diagram really tight
 * #fontSize: 8
 * #spacing: 12
 * #padding: 3
 * Create your own styles
 * #.box: fill=#8f8 dashed
 * [&lt;box&gt; GreenBox]
 * </pre>
 * <p>Documentation:</p>
 * <pre>
 *     Link types:
 *     (Association)
 *     -
 *     -&gt;
 *     &lt;-
 *     &lt;-&gt;
 *     (Dependency)
 *     --
 *     --&gt;
 *     &lt;--
 *     &lt;--&gt;
 *     (generalization or inheritance)
 *     -:&gt;
 *     &lt;:-
 *     (implementation)
 *     --:&gt;
 *     &lt;:--
 *     (composition)
 *     +-
 *     +-&gt;
 *     -+
 *     &lt;-+
 *     (aggregation)
 *     o-
 *     o-&gt;
 *     -o
 *     &lt;-o
 *     (note)
 *     --
 *     (hidden)
 *     -/-
 * </pre>
 * <pre>
 * Classifier types:
 * [name]
 * [&lt;abstract&gt; name]
 * [&lt;instance&gt; name]
 * [&lt;note&gt; name]
 * [&lt;reference&gt; name]
 * [&lt;package&gt; name]
 * [&lt;frame&gt; name]
 * [&lt;database&gt; name]
 * [&lt;start&gt; name]
 * [&lt;end&gt; name]
 * [&lt;state&gt; name]
 * [&lt;choice&gt; name]
 * [&lt;input&gt; name]
 * [&lt;sender&gt; name]
 * [&lt;receiver&gt; name]
 * [&lt;transceiver&gt; name]
 * [&lt;actor&gt; name]
 * [&lt;usecase&gt; name]
 * [&lt;label&gt; name]
 * [&lt;hidden&gt; name]
 * </pre>
 * <pre>
 * Directives:
 * #arrowSize: 1
 * #bendSize: 0.3
 * #direction: down | right
 * #gutter: 5
 * #edgeMargin: 0
 * #edges: hard | rounded
 * #background: transparent
 * #fill: #eee8d5; #fdf6e3
 * #fillArrows: false
 * #font: Calibri
 * #fontSize: 12
 * #leading: 1.25
 * #lineWidth: 3
 * #padding: 8
 * #spacing: 40
 * #stroke: #33322E
 * #title: filename
 * #zoom: 1
 * #acyclicer: greedy
 * #ranker: network-simplex | tight-tree | longest-path
 * #width: 800
 * #height: 600
 * </pre>
 * <pre>
 * Custom classifier styles:
 * A directive that starts with "." define a classifier style. The style is written as a space separated list of modifiers and key/value pairs.
 * #.box: fill=#8f8 dashed
 * #.blob: visual=ellipse
 * [&lt;box&gt; GreenBox]
 * [&lt;blob&gt; HideousBlob]
 * Available key/value pairs are
 * fill=(any css color)
 *
 * stroke=(any css color)
 *
 * align=center
 * align=left
 *
 * direction=right
 * direction=down
 *
 * visual=actor
 * visual=class
 * visual=database
 * visual=ellipse
 * visual=end
 * visual=frame
 * visual=hidden
 * visual=input
 * visual=none
 * visual=note
 * visual=package
 * visual=receiver
 * visual=rhomb
 * visual=roundrect
 * visual=sender
 * visual=start
 * visual=transceiver
 *
 * Available modifiers are:
 * bold
 * underline
 * italic
 * dashed
 * empty
 * </pre>
 * <pre>
 * Command to stop execution:
 * stop
 * </pre>
 * <p>Support for variables - You can define variables to assign classifiers or character sequences to it and use it
 * as a short-form later by enclosing it in square-brackets. Variables are case-sensitive.
 * Please see the example below:</p>
 * <pre>
 * var gs = | double getSpeed()
 * var s = [HasSpeed [gs]]
 * var c = [Car [gs]]
 * var a = [Aircraft [gs]]]
 * [s] &lt;:-- [c]
 * [s] &lt;:-- [a]
 * </pre>
 *
 * @author Syam (Credits to Daniel Kallin for the nomnoml webcomponent)
 */
@NpmPackage(value = "nomnoml", version = "1.5.2")
@Tag("so-diagram")
@JsModule("./so/flow-diagram/flow-diagram.js")
public class FlowDiagram extends LitComponent implements HasSize {

    private final List<String> commands = new ArrayList<>();
    private final Map<String, String> variables = new HashMap<>();
    private Debugger debugger;

    public FlowDiagram() {
        this(null, null);
    }

    public FlowDiagram(int width, int height) {
        this(width + "px", height + "px");
    }

    public FlowDiagram(String width, String height) {
        this(null, width, height);
    }

    public FlowDiagram(TextContent definition) {
        this(definition, null, null);
    }

    private FlowDiagram(TextContent source, String width, String height) {
        getElement().setProperty("idFD", ID.newID());
        if(width != null) {
            setWidth(width);
        }
        if(height != null) {
            setHeight(height);
        }
        if(source != null) {
            draw(source);
        }
    }

    private void drawInt(CharSequence definition) {
        executeJS("updateDef", definition.toString()).
                then(s -> {
                    if(debugger != null) {
                        debugger.debug();
                    }
                }, f -> {
                    if(debugger != null) {
                        debugger.failed();
                    }
                });
    }

    public void draw() {
        if(commands.isEmpty()) {
            if(debugger != null) {
                debugger.failureConsumer.accept(null);
                debugger = null;
            }
            clear();
        } else {
            drawInt(String.join("\n", commands));
            clearCommands();
        }
    }

    public void draw(TextContent definition) {
        if(definition == null) {
            clear();
        } else {
            clearCommands();
            command(definition.getContent());
            draw();
        }
    }

    public void draw(String... comands) {
        clearCommands();
        command(comands);
        draw();
    }

    public void clear() {
        drawInt("#.b:visual=none stroke=transparent\n[<b>B]");
    }

    public void command(String... commands) {
        if(commands != null) {
            for(String c: commands) {
                comm(c);
            }
        }
    }

    public void debug(String command, Consumer<String> failedCommandConsumer) {
        if(failedCommandConsumer == null) {
            drawInt(command);
            return;
        }
        debugger = new Debugger(command, failedCommandConsumer);
        debugger.debug();
    }

    private void comm(String c) {
        if(c == null) {
            return;
        }
        BufferedReader r = IO.get(new StringReader(c));
        String command;
        try {
            while((command = r.readLine()) != null) {
                command = process(command);
                if(command != null) {
                    if("stop".equals(command)) {
                        break;
                    }
                    commands.add(command);
                }
            }
        } catch(IOException ignored) {
        }
    }

    public void clearCommands() {
        commands.clear();
        variables.clear();
    }

    private String process(String c) {
        if(c == null) {
            return null;
        }
        c = c.trim();
        if(c.isEmpty()) {
            return null;
        }
        if("stop".equalsIgnoreCase(c)) {
            return "stop";
        }
        if(c.startsWith("var ")) {
            c = c.substring(4);
            int p = c.indexOf('=');
            if(p <= 0) {
                return null;
            }
            String v = "[" + c.substring(0, p).trim() + "]";
            c = c.substring(p + 1).trim();
            boolean block = c.startsWith("[") && c.endsWith("]");
            String s;
            if(block) {
                s = c.substring(1);
                s = s.substring(0, s.length() - 1);
                p = s.indexOf('>');
                if(p > 0) {
                    s = s.substring(p + 1).trim();
                }
                p = s.indexOf('|');
                if(p > 0) {
                    s = s.substring(0, p).trim();
                }
                p = s.indexOf('[');
                if(p > 0) {
                    s = s.substring(0, p).trim();
                }
                s = "[" + s + "]";
            } else {
                s = c;
            }
            variables.put(v, s);
            return block ? vars(c) : null;
        }
        String cc = c.replace(" ", "").toLowerCase();
        if(cc.startsWith("#width:")) {
            cc = cc.substring(7);
            if(!cc.isEmpty()) {
                setWidth(cc);
            }
            return null;
        }
        if(cc.startsWith("#height:")) {
            cc = cc.substring(8);
            if(!cc.isEmpty()) {
                setHeight(cc);
            }
            return null;
        }
        return vars(c);
    }

    private String vars(String c) {
        for(int i = 0; i < 10; i++) {
            for(String k : variables.keySet()) {
                c = c.replace(k, variables.get(k));
            }
            String command = c;
            if(variables.keySet().stream().noneMatch(command::contains)) {
                break;
            }
        }
        return c;
    }

    private class Debugger {

        private final Consumer<String> failureConsumer;
        private final List<String> lines = new ArrayList<>();
        private int line = 0;

        private Debugger(String command, Consumer<String> failureConsumer) {
            this.failureConsumer = failureConsumer;
            IO.get(new StringReader(command)).lines().forEach(lines::add);
        }

        private void debug() {
            clearCommands();
            while(debugger != null) {
                int count = commands.size();
                debugInt();
                if(commands.size() != count) {
                    break;
                }
            }
            if(debugger != null) {
                draw();
            }
        }

        private void debugInt() {
            ++line;
            if(line > lines.size()) {
                failureConsumer.accept(null);
                debugger = null;
                return;
            }
            clearCommands();
            String command;
            for(int i = 0; i < line; i++) {
                command = process(lines.get(i));
                if(command != null) {
                    if("stop".equals(command)) {
                        failureConsumer.accept(null);
                        debugger = null;
                        return;
                    }
                    commands.add(command);
                }
            }
        }

        private void failed() {
            if(debugger == null) {
                return;
            }
            while(lines.size() > line) {
                lines.remove(lines.size() - 1);
            }
            StringBuilder sb = new StringBuilder();
            String c;
            for(int i = 0; i < lines.size(); i++) {
                c = lines.get(i);
                sb.append(i + 1).append(": ").append(c).append('\n');
                c = process(c);
                if(c != null) {
                    sb.append(i + 1).append(": ").append(c).append('\n');
                }
            }
            sb.append("Error in line ").append(lines.size());
            failureConsumer.accept(sb.toString());
            debugger = null;
        }
    }
}