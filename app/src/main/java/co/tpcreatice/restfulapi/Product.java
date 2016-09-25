package co.tpcreatice.restfulapi;

public class Product {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    private int id = 0;
    private String task = "" ;
    private int status = 0;
    private String created_at = "";


    public Product(){

    }
    public Product(Product task){

        this.setId(task.id);
        this.setTask(task.task);
        this.setStatus(task.status);
        this.setCreated_at(task.created_at);

    }

    public String toString(){
        return task ;
    }




}
