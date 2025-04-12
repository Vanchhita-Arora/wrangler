package io.cdap.wrangler.api;

import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.CompileStatus;
import io.cdap.wrangler.api.RecipeParser;

import java.util.List;
import java.util.ArrayList;

/**
 * A class that simulates the execution of a RecipePipeline.
 */
public class RecipePipeline {

    private List<String> data; // A list to hold processed data
    private List<Directive> directives; // List of directives to apply

    // Constructor to initialize directives and data list
    public RecipePipeline(List<Directive> directives) {
        this.directives = directives;
        this.data = new ArrayList<>();
    }

    /**
     * Executes the list of directives on the data.
     * In a real implementation, each directive would modify the data in some way.
     * @return RecipePipeline with processed data
     */
    public RecipePipeline execute() {
        // Simulate directive execution by modifying the data list
        for (Directive directive : directives) {
            // For now, we just simulate adding processed data from the directive
            data.add("Processed with directive: " + directive.getClass().getSimpleName());
        }
        return this;
    }

    /**
     * Returns the list of processed rows of data.
     * @return List of processed data
     */
    public List<String> rows() {
        return data;
    }

    /**
     * A placeholder method to simulate compiling the recipe's directives.
     * @return CompileStatus indicating the result of the compile operation
     */
    // public CompileStatus compile() {
    //     // Simulate compile status - would typically involve checking the validity of the directives
    //     return new CompileStatus() {
    //         @Override
    //         public List<Directive> getLoadableDirectives() {
    //             return directives; // Return the list of directives for the test
    //         }
    //     };
    // }

    // Main method for testing purposes
    public static void main(String[] args) {
        // Creating a mock list of directives (Replace with actual directives for real tests)
        List<Directive> directives = new ArrayList<>();
        // directives.add(new Directive() {
        //     // You can define some mock behavior for the directive here
        // });

        // Creating and executing the pipeline
        RecipePipeline pipeline = new RecipePipeline(directives);
        RecipePipeline executedPipeline = pipeline.execute();

        // Printing out the result of the executed pipeline
        executedPipeline.rows().forEach(System.out::println);
    }
}
