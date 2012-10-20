package thug.c.core;

public interface Type {
   public String getCTypeString();

   public String getSelfTypeString();

   public void register(TypingEnvironment env);
}
