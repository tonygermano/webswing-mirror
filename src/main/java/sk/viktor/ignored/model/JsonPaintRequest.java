package sk.viktor.ignored.model;
public class JsonPaintRequest {
        public String type="paint";
        public String filename;
        public int x;
        public int y;
        public long seq;

        public JsonPaintRequest(long seq, String filename, int x, int y) {
            super();
            this.seq=seq;
            this.filename = filename;
            this.x = x;
            this.y = y;
        }
        
        public String getFilename() {
            return filename;
        }
        
        public void setFilename(String filename) {
            this.filename = filename;
        }
        
        public int getX() {
            return x;
        }
        
        public void setX(int x) {
            this.x = x;
        }
        
        public int getY() {
            return y;
        }
        
        public void setY(int y) {
            this.y = y;
        }

        
        public long getSeq() {
            return seq;
        }

        
        public void setSeq(long seq) {
            this.seq = seq;
        }
    }