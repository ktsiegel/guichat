package emoticons;

public class Emoticon {
    public final String text;

    public Emoticon(String text) {
        this.text = text;
    }
    
    public static boolean isValid(String text) {
        return (new Emoticon(text)).getURL() != null;
    }

    public String getURL() {
        if (text.equals("(:")) {
            return "emoticon__backwards_smile.gif";
        } else if (text.equals("</3")) {
            return "emoticon_broken_heart.gif";
        } else if (text.equals(":bunny")) {
            return "emoticon_bunny.gif";
        } else if (text.equals(":camel")) {
            return "emoticon_camel.gif";
        } else if (text.equals(":cat")) {
            return "emoticon_cat.gif";
        } else if (text.equals(":cow")) {
            return "emoticon_cow.gif";
        } else if (text.equals(":'(")) {
            return "emoticon_cry.gif";
        } else if (text.equals(":dog")) {
            return "emoticon_dog.gif";
        } else if (text.equals(":dolphin")) {
            return "emoticon_dolphin.gif";
        } else if (text.equals(":duck")) {
            return "emoticon_duck.gif";
        } else if (text.equals(":elephant")) {
            return "emoticon_elephant.gif";
        } else if (text.equals(":fish")) {
            return "emoticon_fish.gif";
        } else if (text.equals(":frog")) {
            return "emoticon_frog.gif";
        } else if (text.equals("<3")) {
            return "emoticon_heart.gif";
        } else if (text.equals(":horse")) {
            return "emoticon_horse.gif";
        } else if (text.equals(":koala")) {
            return "emoticon_koala.gif";
        } else if (text.equals(":D")) {
            return "emoticon_laugh.gif";
        } else if (text.equals(">.>")) {
            return "emoticon_left.gif";
        } else if (text.equals(">:(")) {
            return "emoticon_mad.gif";
        } else if (text.equals(":-/")) {
            return "emoticon_meh.gif";
        } else if (text.equals(":$")) {
            return "emoticon_money.gif";
        } else if (text.equals(":mouse")) {
            return "emoticon_mouse.gif";
        } else if (text.equals(":O")) {
            return "emoticon_O.gif";
        } else if (text.equals(":penguin")) {
            return "emoticon_penguin.gif";
        } else if (text.equals(":pig")) {
            return "emoticon_pig.gif";
        } else if (text.equals("~@~")) {
            return "emoticon_poop.gif";
        } else if (text.equals(":(")) {
            return "emoticon_sad.gif";
        } else if (text.equals(":sheep")) {
            return "emoticon_sheep.gif";
        } else if (text.equals(":)")) {
            return "emoticon_smile.gif";
        } else if (text.equals(":snake")) {
            return "emoticon_snake.gif";
        } else if (text.equals(":P")) {
            return "emoticon_tongue.gif";
        } else if (text.equals(";)")) {
            return "emoticion_wink.gif";
        } else {
            return null;
        }
    }
}
