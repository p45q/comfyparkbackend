package ch.ffhs.comfypark.model;

public class AtGateRequest {
   public String gateUUID;
   public String customerID;

   public String getFirstName() {
       return gateUUID;
   }

   public void setFirstName(String firstName) {
       this.gateUUID = firstName;
   }

   public String getLastName() {
       return customerID;
   }

   public void setLastName(String lastName) {
       this.customerID = lastName;
   }

   public AtGateRequest(String firstName, String lastName) {
       this.gateUUID = firstName;
       this.customerID = lastName;
   }

   public AtGateRequest() {
   }
}