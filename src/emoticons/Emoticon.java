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
            return "emoticon_backwards_smile.gif";
        } else if (text.equals("</3")) {
            return "emoticons_broken_heart.gif";
        } else if (text.equals(":bunny")) {
            return "emoticons_bunny.gif";
        } else if (text.equals(":camel")) {
            return "emoticons_camel.gif";
        } else if (text.equals(":cat")) {
            return "emoticons_cat.gif";
        } else if (text.equals(":cow")) {
            return "emoticons_cow.gif";
        } else if (text.equals(":'(")) {
            return "emoticons_cry.gif";
        } else if (text.equals(":dog")) {
            return "emoticons_dog.gif";
        } else if (text.equals(":dolphin")) {
            return "emoticons_dolphin.gif";
        } else if (text.equals(":duck")) {
            return "emoticons_duck.gif";
        } else if (text.equals(":elephant")) {
            return "emoticons_elephant.gif";
        } else if (text.equals(":fish")) {
            return "emoticons_fish.gif";
        } else if (text.equals(":frog")) {
            return "emoticons_frog.gif";
        } else if (text.equals("<3")) {
            return "emoticons_heart.gif";
        } else if (text.equals(":horse")) {
            return "emoticons_horse.gif";
        } else if (text.equals(":koala")) {
            return "emoticons_koala.gif";
        } else if (text.equals(":D")) {
            return "emoticons_laugh.gif";
        } else if (text.equals(">.>")) {
            return "emoticons_left.gif";
        } else if (text.equals(">:(")) {
            return "emoticons_mad.gif";
        } else if (text.equals(":-/")) {
            return "emoticons_meh.gif";
        } else if (text.equals(":$")) {
            return "emoticons_money.gif";
        } else if (text.equals(":mouse")) {
            return "emoticons_mouse.gif";
        } else if (text.equals(":O")) {
            return "emoticons_O.gif";
        } else if (text.equals(":penguin")) {
            return "emoticons_penguin.gif";
        } else if (text.equals(":pig")) {
            return "emoticons_pig.gif";
        } else if (text.equals("~@~")) {
            return "emoticons_poop.gif";
        } else if (text.equals(":(")) {
            return "emoticons_sad.gif";
        } else if (text.equals(":sheep")) {
            return "emoticons_sheep.gif";
        } else if (text.equals(":)")) {
            return "emoticons_smile.gif";
        } else if (text.equals(":snake")) {
            return "emoticons_snake.gif";
        } else if (text.equals(":P")) {
            return "emoticons_tongue.gif";
        } else if (text.equals(";)")) {
            return "emoticions_wink.gif";
        } else {
            return null;
        }
    }
}
