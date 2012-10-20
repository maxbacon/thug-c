package thug.c.core;

public class LatentFinal<T> {
   private T data;

   public LatentFinal() {
      this.data = null;
   }

   public void set(T data) {
      if (this.data != null) {
         throw new IllegalStateException("sorry");
      }
      this.data = data;
   }

   public T get() {
      return data;
   }
}
