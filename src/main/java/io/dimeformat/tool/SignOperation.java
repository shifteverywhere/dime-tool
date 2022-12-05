package io.dimeformat.tool;

import io.dimeformat.Item;
import io.dimeformat.Key;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SignOperation extends Operation {

    public static final String NAME = "sign";

    @Override
    public String getDescription() {
        return "Signs a Dime item with a provided key.";
    }

    @Override
    public List<Option> getOptions() {
        ArrayList<Option> array = new ArrayList<>();
        Collections.addAll(array,
                new Option(Option.ITEM, true, "Dime encoded item or envelope to sign."),
                new Option(Option.KEY, true, "Dime encoded key to sign the item."));
        return array;
    }

    @Override
    public String execute() throws Exception {
        String encodedItem = arguments.get(Option.ITEM);
        if (encodedItem == null || encodedItem.length() == 0) {
            DimeTool.showErrorMessage(this, "Missing required option: " + Option.ITEM + ".", true);
            return null;
        }
        Item item = Item.importFromEncoded(encodedItem);
        String encodedKey = arguments.get(Option.KEY);
        if (encodedKey == null) {
            DimeTool.showErrorMessage(this, "Missing required option: " + Option.KEY + ".", true);
            return null;
        }
        Key key = Item.importFromEncoded(encodedKey);
        item.sign(key);
        return item.exportToEncoded();
    }

}
