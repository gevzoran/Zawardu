package engine;

import imgui.*;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;
import scenes.Scene;

public class ImGuiLayer {
    private boolean showText = false;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private long windowPtr;
    public ImVec2 windowPos = new ImVec2();

    public ImGuiLayer(long windowPtr){
        this.windowPtr = windowPtr;
    }
    public void drawGui(Scene currentScene){
        beginDraw();
        setupDockSpace();
        currentScene.sceneImGui();
        ImGui.showDemoWindow();
        ImGui.end();
        render();
    }

    public void destroy(){
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }

    public void init(){
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        imGuiGlfw.init(windowPtr,true);


        // fonts
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, need destroy

        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontFromFileTTF("src/main/resources/fonts/segoeui.ttf",24,fontConfig);
        fontConfig.destroy();


        imGuiGl3.init( "#version 330");
        imGuiGl3.newFrame();

    }

    public ImGuiImplGlfw getImGuiGlfw() {
        return imGuiGlfw;
    }

    public ImGuiImplGl3 getImGuiGl3() {
        return imGuiGl3;
    }

    public void beginDraw(){
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void render(){
        ImGui.render();
        this.getImGuiGl3().renderDrawData(ImGui.getDrawData());
        if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)){
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(windowPtr);
        }
    }

    private void setupDockSpace(){
        int windowFlags =  ImGuiWindowFlags.NoDocking;
        ImGui.setNextWindowPos(0,24, ImGuiCond.Always); //TODO fix this 24 crap
        ImGui.setNextWindowSize(Window.getWidth(),Window.getHeight() - 24);
        ImGui.setNextWindowViewport(ImGui.getMainViewport().getID());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding,0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize,0);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                       ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                       ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Dockspace Demo", new ImBoolean(true),windowFlags );
        ImGui.popStyleVar(2);
        ImGui.getWindowPos(windowPos);
        ImGui.dockSpace(ImGui.getID("Dockspace"));
    }
}
