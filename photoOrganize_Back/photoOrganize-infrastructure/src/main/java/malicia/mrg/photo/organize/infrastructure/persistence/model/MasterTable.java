package malicia.mrg.photo.organize.infrastructure.persistence.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class MasterTable {


        @Id
        @GeneratedValue(strategy= GenerationType.AUTO)
        private UUID id;
        private String msg;
        protected MasterTable() {}

        public MasterTable(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return String.format(
                    "Customer[id=%s, msg='%s']",
                    id, msg);
        }

        public UUID getId() {
            return id;
        }

        public String getMsg() {
            return msg;
        }

    }