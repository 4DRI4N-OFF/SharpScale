package dev.instinct.guianimqa;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import java.nio.file.Files;
public class QA implements net.fabricmc.api.ClientModInitializer {
 static boolean finished=false;static boolean size=false;static int wait=0,index=24*6,t=-1;static final String[] SAMPLES={"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz", "0123456789 áéíóú ñü ¿¡ €", "Il1 O0 [] () {} -> !=", "中文 日本語 한국어", "Bold and italic text"};
 public void onInitializeClient(){}
 public static void frame(Minecraft mc){
  if(finished)return;
  if(!size){mc.getWindow().setWindowed(3840,2160);size=true;}
  if(t<0){if(mc.gui.screen() instanceof TitleScreen && mc.gui.overlay()==null && ++wait>100)t=0;return;}
  int step=index/6, mode=index%6;boolean mono=mode<3;
  if(t==0){dev.instinct.sharpscale.SharpScale.steps=step;dev.instinct.sharpscale.SharpScale.option().set(step);dev.instinct.sharpscale.SharpScale.monoFont=mono;mc.resizeGui();Screen s=mode%3==0?new Sample():mode%3==1?new dev.instinct.sharpscale.SharpScaleScreen(new TitleScreen()):new dev.instinct.guianim.ConfigScreen(new TitleScreen());mc.gui.setScreen(s);}
  if(t==38){Screenshot.grab(mc.gameDirectory,String.format("hi-scale%02d-%s-%d.png",step,mono?"mono":"vanilla",mode%3),mc.gameRenderer.mainRenderTarget(),1,c->{});try{Files.writeString(mc.gameDirectory.toPath().resolve("hi-scale-proof.csv"),step+","+mono+","+mode%3+","+dev.instinct.sharpscale.SharpScale.current+","+mc.gui.screen().width+","+mc.gui.screen().height+"\n",java.nio.file.StandardOpenOption.CREATE,java.nio.file.StandardOpenOption.APPEND);}catch(Exception e){throw new RuntimeException(e);}}
  if(t==40 && mode%3==2)mc.gui.screen().mouseScrolled(0,0,0,-100);
  if(t==60 && mode%3==2)Screenshot.grab(mc.gameDirectory,String.format("hi-scale%02d-%s-bottom.png",step,mono?"mono":"vanilla"),mc.gameRenderer.mainRenderTarget(),1,c->{});
  if(++t>65){t=0;index++;if(index>=33*6){try{Files.writeString(mc.gameDirectory.toPath().resolve("hi-scale-done.txt"),"54 cases / 72 screenshots");}catch(Exception e){throw new RuntimeException(e);}finished=true;mc.stop();}}
 }
 static class Sample extends Screen {
  Sample(){super(Component.literal("Font scale QA"));}
  public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float dt){g.fill(0,0,width,height,0xFF111318);int y=12;for(String s:SAMPLES){Component c=Component.literal(s);if(s.startsWith("Bold"))c=c.copy().withStyle(st->st.withBold(true).withItalic(true));g.textWithWordWrap(font,c,8,y,width-16,0xFFFFFFFF);y+=24;}g.text(font,"step="+dev.instinct.sharpscale.SharpScale.steps+" effective="+dev.instinct.sharpscale.SharpScale.current,8,height-15,0xFF8ADFCC);}
 }
}
