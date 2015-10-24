package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class TesterHomeDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.testerhome.nativeandroid.dao");
        initTopic(schema);
        initUser(schema);
        new DaoGenerator().generateAll(schema, args[0]);

    }



    private static void initUser(Schema schema) {
        Entity user = schema.addEntity("UserDB");
        user.implementsSerializable();
        user.setHasKeepSections(true);
        user.addLongProperty("user_id").primaryKey();
        user.addStringProperty("login");
        user.addStringProperty("name");
        user.addStringProperty("avatar_url");
    }

    private static void initTopic(Schema schema) {
        Entity topic = schema.addEntity("TopicDB");
        topic.implementsSerializable();
        topic.setHasKeepSections(true);
        topic.addLongProperty("id").primaryKey();
        topic.addStringProperty("title");
        topic.addBooleanProperty("is_hot");
        topic.addBooleanProperty("is_excellent");
        topic.addLongProperty("created_at");
        topic.addLongProperty("updated_at");
        topic.addLongProperty("replied_at");
        topic.addIntProperty("replies_count");
        topic.addIntProperty("node_id");
        topic.addStringProperty("node_name");
        topic.addIntProperty("last_reply_user_id");
        topic.addStringProperty("last_reply_user_login");
        topic.addIntProperty("user_id");
    }

}
