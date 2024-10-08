package com.example.planetopedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.planetopedia.databinding.FragmentUpdatePlanetBinding

class UpdatePlanetFragment : Fragment() {
    // Declare a private lateinit variable of type PlanetViewModel
    // This variable will be used to interact with the ViewModel associated with this fragment
    private lateinit var viewModel: PlanetViewModel

    // Declare a property that retrieves the arguments passed to this fragment
    // This property uses the by navArgs() delegate to get the arguments using the safe args feature of the Navigation component
    // The type of the property is generated by the safe args plugin, and will depend on the arguments passed to the fragment
    private val args: UpdatePlanetFragmentArgs by navArgs()

    // Declare a nullable private property of type FragmentUpdatePlanetBinding
    // This property will hold the binding object for the fragment's layout, which is generated at runtime
    private var _binding: FragmentUpdatePlanetBinding? = null

    // Declare a non-nullable property of type FragmentUpdatePlanetBinding, which retrieves the binding object
    // This property uses the getter function to return the binding object when called
    // It throws an exception if the binding object is null, which should only occur if the fragment's view is destroyed
    private val binding get() = _binding!!

    // Define a companion object, which is a singleton object associated with the UpdatePlanetFragment class
    companion object {
        // Define a regular expression pattern for validating planet names
        // The pattern requires the name to start with an uppercase letter followed by zero or more lowercase letters
        // ^ - Start of the string
        // [A-Z] - An uppercase letter (A to Z)
        // [a-z]* - Zero or more lowercase letters (a to z)
        // $ - End of the string
        private val PLANET_NAME_PATTERN = "^[A-Z][a-z]*$".toRegex()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatePlanetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get ViewModel
        viewModel = ViewModelProvider(this)[PlanetViewModel::class.java]

        // Set up UI with the passed Planet argument
        val planet = args.planet
        binding.planetName.setText(planet.name)
        binding.distanceFromSun.setText(planet.distanceFromSun.toString())
        binding.gravity.setText(planet.gravity.toString())

        // Update button listener
        binding.updateButton.setOnClickListener {
            val name = binding.planetName.text.toString()
            val distance = binding.distanceFromSun.text.toString()
            val gravity = binding.gravity.text.toString()

            // Add a list of valid planet names
            val validPlanetNames = listOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")

            if (name.isNotEmpty() && distance.isNotEmpty() && gravity.isNotEmpty()) {
                // Check if the planet name is valid
                if (name in validPlanetNames) {
                    // Check if the name already exists in the database
                    viewModel.isDuplicateName(planet.id, name).observe(viewLifecycleOwner) { isDuplicate ->
                        if (!isDuplicate) {
                            // Create updated Planet object and update it in the database
                            val updatedPlanet = Planet(planet.id, name, distance.toDouble(), gravity.toDouble(), planet.imageName)
                            viewModel.update(updatedPlanet)
                            // Navigate back to the list of planets
                            findNavController().navigate(R.id.action_updatePlanetFragment_to_planetsListFragment)
                        } else {
                            // Show an error message if the name is a duplicate
                            Toast.makeText(context, "A planet with this name already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Show an error message if the planet name is not valid
                    Toast.makeText(context, "Please enter a valid planet name", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show an error message if any of the fields are empty
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
