import android.annotation.SuppressLint

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.class_attendace_app.ClassModel
import com.example.class_attendace_app.R

class ClassAdapter(
    private var classList: List<ClassModel>, // List of class models
    private val onClick: (ClassModel) -> Unit // Click handler
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    // Update the class list with new data
    @SuppressLint("NotifyDataSetChanged")
    fun updateClassList(newClassList: List<ClassModel>) {
        classList = newClassList
        notifyDataSetChanged() // Notify the adapter to refresh the entire list
    }

    // Create ViewHolder by inflating the class card layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_class_card, parent, false)
        return ClassViewHolder(view)
    }

    // Bind the data to the ViewHolder
    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val currentClass = classList[position]
        holder.className.text = currentClass.name
        holder.classDescription.text = currentClass.description
        holder.courseCode.text = currentClass.courseCode

        // Set onClick listener for the item view
        holder.itemView.setOnClickListener { onClick(currentClass) }
    }

    // Get the item count (size of the class list)
    override fun getItemCount(): Int = classList.size

    // ViewHolder class to hold references to the class card views
    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val className: TextView = itemView.findViewById(R.id.class_name)
        val classDescription: TextView = itemView.findViewById(R.id.class_description)
        val courseCode: TextView = itemView.findViewById(R.id.class_code)
    }
}
