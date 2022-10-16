module com.geekbrains.common {
    requires lombok;
    requires io.netty.all;
    requires org.apache.commons.collections4;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;

    exports com.geekbrains;
    exports com.geekbrains.model;
}